package com.miaoshaproject.service.impl;

import com.miaoshaproject.daoobject.OrderDO;
import com.miaoshaproject.daoobject.SequenceDO;
import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.error.EmBusinessError;
import com.miaoshaproject.mapper.OrderDOMapper;
import com.miaoshaproject.mapper.SequenceDOMapper;
import com.miaoshaproject.service.ItemService;
import com.miaoshaproject.service.OrderService;
import com.miaoshaproject.service.UserDOService;
import com.miaoshaproject.service.model.ItemModel;
import com.miaoshaproject.service.model.OrderModel;
import com.miaoshaproject.service.model.UserModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private SequenceDOMapper sequenceDOMapper;
    @Autowired
    private OrderDOMapper orderDOMapper;
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserDOService userDOService;
    @Override
    @Transactional
    public OrderModel creatOrder(Integer userId, Integer itemId, Integer amount,Integer promoId) throws BusinessException {
        //校验下单状态，下单的商品是否存在，用户是否合法，购买数量是否正确
        ItemModel itemModel = itemService.getItemById(itemId);
        if (itemModel == null) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "商品信息不存在");
        }
        UserModel userModel = userDOService.qeuryUserById(userId);
        if (userModel == null) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "用户信息不存在");
        }
        if (amount <= 0 || amount > 99) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "数量信息不正确");
        }
        //校验活动信息
        if (promoId != null) {
            //(1)校验对应活动是否存在适用商品
            if (promoId.intValue() != itemModel.getPromoModel().getId()) {
                throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "活动信息不正确");
            } else if (itemModel.getPromoModel().getStatus() != 2) {
                //(2)校验活动是否正在进行
                throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "活动还不没开始");
            }
        }

        //落单减库存，支付减库存
        boolean resutl = itemService.decreaseStock(itemId, amount);
        if (!resutl) {
            throw new BusinessException(EmBusinessError.STOCK_NOT_ENOUGH);
        }

        //订单入库
        OrderModel orderModel = new OrderModel();
        orderModel.setUserId(userId);
        orderModel.setItemId(itemId);
        orderModel.setAmount(amount);
        if (promoId != null) {
            orderModel.setItemPrice(itemModel.getPromoModel().getPromoItemPrice());
        } else {
            orderModel.setItemPrice(itemModel.getPrice());

        }
        orderModel.setPromoId(promoId);
        orderModel.setOrderPrice(orderModel.getItemPrice().multiply(new BigDecimal(amount)));

        //生成交易流水号,订单号
        orderModel.setId(generateOrderNO());
        OrderDO orderDO = this.converOrderDOFromOrderMOdel(orderModel);
        orderDOMapper.insertSelective(orderDO);

        //加上商品销量
        itemService.increaseSales(itemId, amount);

        //返回前端

        return orderModel;
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String generateOrderNO(){
        //订单有16位
        StringBuilder stringBuilder = new StringBuilder();
        //前8位为时间信息，年月日
        LocalDateTime now = LocalDateTime.now();
        String nowDate = now.format(DateTimeFormatter.ISO_DATE).replace("-","");
        stringBuilder.append(nowDate);


        //中间6位位自增序列
        //获取当前sequence
        int sequence ;
        SequenceDO sequenceDO = sequenceDOMapper.getSequenceByName("order_info");
        sequence = sequenceDO.getCurrentValue();
        sequenceDO.setCurrentValue(sequence + sequenceDO.getStep());
        sequenceDOMapper.updateByPrimaryKeySelective(sequenceDO);
        String sequenceStr = String.valueOf(sequence);
        for (int i = 0; i < 6 - sequenceStr.length(); i++) {
            stringBuilder.append("0");
        }
        stringBuilder.append(sequenceStr);
        //最后两位为份库分表位,暂时写死
        stringBuilder.append("00");
        return stringBuilder.toString();
    }


    private OrderDO converOrderDOFromOrderMOdel(OrderModel orderModel) {
        if (orderModel == null) {
            return null;
        }
        OrderDO orderDO = new OrderDO();
        BeanUtils.copyProperties(orderModel, orderDO);
        orderDO.setItemPrice(orderModel.getItemPrice().doubleValue());
        orderDO.setOrderPrice(orderModel.getOrderPrice().doubleValue());
        return orderDO;
    }

}
