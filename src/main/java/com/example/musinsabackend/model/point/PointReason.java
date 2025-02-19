package com.example.musinsabackend.model.point;

public enum PointReason {
    PURCHASE_REWARD,   // 구매 시 적립
    REVIEW_REWARD,     // 후기 작성 시 적립
    EVENT_REWARD,      // 이벤트 참여 적립
    ORDER_USE,         // 주문 시 사용
    ORDER_CANCEL,      // 주문 취소로 인한 차감
    EXTINCTION         // 만료로 인한 차감
}
