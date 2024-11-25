<template>
  <div class="container">
    <!-- 상품 목록 -->
    <div class="product-list">
      <div v-for="product in products" :key="product.id" class="product-item">
        <!-- 등급 아이콘 -->
        <div class="grade-icon" :class="product.grade.toLowerCase()">
          {{ product.grade }}
        </div>

        <!-- 상품 정보 -->
        <div class="product-info">
          <div class="product-header">
            <span class="vendor">{{ product.vendor }}</span>
            <h3 class="product-name">{{ product.name }}</h3>
          </div>

          <div class="product-notice" v-if="product.notice">
            {{ product.notice }}
          </div>

          <!-- 가격 정보 -->
          <div class="price-info">
            <div class="unit-price">
              (100g 당: {{ product.unitPrice }}원)
              <span class="delivery-type">{{ product.deliveryType }}</span>
              <span class="shipping-fee" v-if="product.shippingFee">
                              {{ product.shippingFee }}
                          </span>
            </div>
            <div class="total-price">
              {{ formatPrice(product.price) }}원
            </div>
          </div>

          <!-- 상품 보기 버튼 -->
          <button class="view-product" @click="goToProduct(product.link)">
            상품보기 >
          </button>
        </div>
      </div>
    </div>

    <!-- 등급 안내 -->
    <div class="grade-guide">
      <div class="guide-title">등급 안내</div>
      <div class="guide-items">
        <div class="guide-item a">
          <span class="grade">A급</span>
          <span class="desc">: 근기업 도축장, HACCP, 냉장, 무항생제</span>
        </div>
        <div class="guide-item a-plus">
          <span class="grade">A+급</span>
          <span class="desc">: 근기업 도축장, HACCP, 급속냉동, 무항생제</span>
        </div>
        <div class="guide-item b">
          <span class="grade">B급</span>
          <span class="desc">: 근기업 도축장, HACCP, 냉장</span>
        </div>
        <div class="guide-item b-plus">
          <span class="grade">B+급</span>
          <span class="desc">: 근기업 도축장, HACCP, 급속냉동</span>
        </div>
        <div class="guide-item c">
          <span class="grade">C급</span>
          <span class="desc">: 도축장 미표기, HACCP, 냉동</span>
        </div>
        <div class="guide-item d">
          <span class="grade">D급</span>
          <span class="desc">: 도축장 미표기, HACCP</span>
        </div>
        <div class="guide-item e">
          <span class="grade">E급</span>
          <span class="desc">: 도축장 미표기</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const products = ref([
  {
    id: 1,
    grade: 'A',
    vendor: '냉장',
    name: '하림-자연실록 무항생제 닭가슴살 1kg',
    notice: '가장 신선도가 높습니다!',
    unitPrice: '997',
    deliveryType: '배송비',
    shippingFee: '3,500원',
    price: 9970,
    link: 'https://www.coupang.com/vp/products/8244230481?itemId=19165669714&vendorItemId=71730861017&q=%EB%8B%AD%EA%B0%80%EC%8A%B4%EC%82%B41kg+%ED%95%98%EB%A6%BC+%EC%9E%90%EC%97%B0%EC%8B%A4%EB%A1%9D&itemsCount=36&searchId=bb87f7a6c8754ad4974339db13c115a6&rank=1&searchRank=1&isAddedCart='
  },
  {
    id: 2,
    grade: 'B',
    vendor: '냉동',
    name: '하림 동물복지 인증 자연실록 IFF 한입 닭가슴살 (냉동)',
    unitPrice: '1,470',
    deliveryType: '배송비 로켓프레쉬',
    price: 14700,
    link: 'https://www.coupang.com/vp/products/8137170483?itemId=23116112190&vendorItemId=90149312611&q=%EB%8B%AD%EA%B0%80%EC%8A%B4%EC%82%B41kg+%ED%95%98%EB%A6%BC+%EC%9E%90%EC%97%B0%EC%8B%A4%EB%A1%9D&itemsCount=36&searchId=bb87f7a6c8754ad4974339db13c115a6&rank=3&searchRank=3&isAddedCart='
  },
  // ... 더 많은 상품 데이터
]);

const formatPrice = (price) => {
  return price.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
};

const goToProduct = (link) => {
  window.open(link, '_blank');
};
</script>

<style scoped>
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.product-item {
  display: flex;
  padding: 20px;
  border-bottom: 1px solid #eee;
  gap: 20px;
}

.grade-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  color: white;
}

.grade-icon.a {
  background-color: #4CAF50;
}

.grade-icon.b {
  background-color: #2196F3;
}

.product-info {
  flex: 1;
}

.product-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.vendor {
  background-color: #f0f0f0;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 14px;
}

.product-name {
  font-size: 18px;
  font-weight: bold;
}

.product-notice {
  color: #666;
  font-size: 14px;
  margin-bottom: 10px;
}

.price-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
}

.unit-price {
  color: #666;
  font-size: 14px;
}

.delivery-type {
  margin-left: 10px;
}

.total-price {
  font-size: 24px;
  font-weight: bold;
  color: #2196F3;
}

.view-product {
  float: right;
  padding: 8px 16px;
  border: none;
  background-color: #2196F3;
  color: white;
  border-radius: 4px;
  cursor: pointer;
  margin-top: 10px;
}

.view-product:hover {
  background-color: #1976D2;
}

.grade-guide {
  margin-top: 30px;
  background-color: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
}

.guide-title {
  font-weight: bold;
  margin-bottom: 10px;
}

.guide-items {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 10px;
}

.guide-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.grade {
  font-weight: bold;
}
</style>