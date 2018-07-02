package com.jt.cart.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jt.cart.mapper.CartMapper;
import com.jt.cart.pojo.Cart;
import com.jt.common.vo.SysResult;

@Service
public class CartService {

    @Autowired
    private CartMapper cartMapper;

    public SysResult saveItem(Cart newCart) {
        // 判断商品是否存在购物车中，如果存在，数量+1
        Cart cart = this.cartMapper.queryCartByUserIdAndItemId(newCart.getUserId(), newCart.getItemId());
        if (cart == null) {
            // 不存在
            this.cartMapper.save(newCart);
            return SysResult.ok(newCart.getId());
        } else {
            // 存在
            cart.setNum(cart.getNum() + 1);
            this.cartMapper.updateCartNum(cart);
            return SysResult.build(202, "该商品已经存在购物车中!商品数量+1", null);
        }
    }

    public SysResult queryCartList(Long userId) {
        List<Cart> carts = this.cartMapper.queryCartList(userId);
        return SysResult.ok(carts);
    }

    /**
     * 修改购物车数量
     * 
     * @param userId
     * @param itemId
     * @param num
     * @return
     */
    public SysResult updateNum(Long userId, Long itemId, Integer num) {
        Integer count = this.cartMapper.updateCartNumByUserIdAndItemId(userId, itemId, num);
        if (count == null || count.intValue() == 0) {
            return SysResult.build(201, "该商品不存在购物车中!");
        }
        return SysResult.ok();
    }

    /**
     * 删除商品
     * 
     * @param userId
     * @param itemId
     * @return
     */
    public SysResult delete(Long userId, Long itemId) {
        Integer count = this.cartMapper.delete(userId, itemId);
        if (count == null || count.intValue() == 0) {
            return SysResult.build(201, "该商品不存在购物车中!");
        }
        return SysResult.ok();
    }

}
