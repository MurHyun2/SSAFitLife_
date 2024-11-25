<template>
  <div class="container">
      <!-- 상단 카테고리 탭 -->
      <div class="category-tabs">
          <button 
              v-for="tab in tabs" 
              :key="tab.id"
              :class="['tab-btn', { active: currentTab === tab.id }]"
              @click="changeTab(tab.id)"
          >
              {{ tab.name }}
          </button>
          <div class="user-level">
              <button class="level-btn">
                  <span class="level-icon">1️⃣</span>
                  A급
              </button>
              <button class="level-btn">
                  <span class="level-icon">2️⃣</span>
                  B급
              </button>
              <button class="level-btn">
                  <span class="level-icon">➕</span>
                  번외 섹션
              </button>
          </div>
      </div>

      <!-- 좌측 서브카테고리 -->
      <div class="content-wrapper">
          <div class="subcategories">
              <div 
                  v-for="category in filteredCategories" 
                  :key="category.id"
                  :class="['subcategory-item', { active: selectedCategory === category.id }]"
                  @click="selectCategory(category.id)"
              >
                  <span class="category-icon">{{ category.icon }}</span>
                  {{ category.name }}
              </div>
          </div>

          <!-- 우측 제품 목록 -->
          <div class="products-section">
              <h2>추천 상품</h2>
              <div class="products-list">
                  <div 
                      v-for="product in filteredProducts" 
                      :key="product.id"
                      class="product-item"
                  >
                      <span class="product-icon">{{ product.icon }}</span>
                      <div class="product-info">
                          <span class="product-name">{{ product.name }}</span>
                      </div>
                      <button class="detail-btn" @click="goToProductLink(product.link)">></button>
                  </div>
              </div>

              <!-- 우측 정보 패널 -->
              <div class="info-panel">
                  <div class="info-section">
                      <h3>섭취량</h3>
                      <p>체중 1kg당 1.6-2g</p>
                  </div>
                  <div class="info-section">
                      <h3>코멘트</h3>
                      <p>초보자는 2g이 좋다, 흰우유(500ml)를 잘 못먹으면 WPI, 잘 먹으면 WPC, 적당히 잘 먹으면 WPI+WPC</p>
                  </div>
                  <div class="info-section">
                      <h3>대상</h3>
                      <p>단백질을 잘 못챙겨먹는 사람</p>
                  </div>
                  <div class="message-box">
                      <span class="emoji">😊</span>
                      <p>운동후 섭취 혹은 단백질이 적은 식사 후, 운동후 식사가 바로 못다면 20g, 운동후 식사가 바로 된다면 40g 되시 3/4컵 바로 먹고 1/4은 30분후 섭취.</p>
                  </div>
              </div>
          </div>
      </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';

const goToProductLink = (link) => {
  window.open(link, '_blank');
};

const tabs = [
  { id: 'beginner', name: '초보자' },
  { id: 'advanced', name: '숙련자' },
  { id: 'health', name: '기타건강' }
];

const categories = {
  beginner: [
      { id: 'protein', name: '단백질', icon: '1️⃣' },
      { id: 'carb', name: '탄수화물', icon: '1️⃣' },
      { id: 'vitamin', name: '종합비타민', icon: '1️⃣' },
      { id: 'probiotic', name: '유산균', icon: '➕' },
  ],
  advanced: [
      { id: 'caffeine', name: '카페인', icon: '1️⃣' },
      { id: 'creatine', name: '크레아틴', icon: '1️⃣' },
      { id: 'betaAlanine', name: '베타알라닌', icon: '1️⃣' }
  ],
  health: [
      { id: 'vitaminD', name: '비타민D', icon: '1️⃣' },
      { id: 'calcium', name: '칼슘', icon: '1️⃣' },
      { id: 'omega3', name: '오메가3', icon: '2️⃣' }
  ]
};

const products = {
  protein: [
      { id: 1, name: '옵티멈 골드 스탠다드 웨이', icon: '🍃' , link: 'https://kr.iherb.com/pr/optimum-nutrition-gold-standard-100-whey-double-rich-chocolate-5-lbs-2-27-kg/27509' },
      { id: 2, name: 'Allmax 웨이프로틴',  icon: '🍃' , link: 'https://kr.iherb.com/pr/allmax-classic-allwhey-100-whey-protein-chocolate-5-lbs-2-27-kg/69100' },
      { id: 3, name: '컴뱃 100%', icon: '🚀' , link: 'https://www.coupang.com/vp/products/6731349444?itemId=3071491018&vendorItemId=4041827021&src=1139000&spec=10799999&addtag=400&ctag=6731349444&lptag=AF7910856&itime=20241124193506&pageType=PRODUCT&pageValue=6731349444&wPcid=17270920882514878723881&wRef=&wTime=20241124193506&redirect=landing&traceid=V0-181-c7a4d267bbb9b282&mcid=26a0c130317e4b3badc382f191d4d8f4&placementid=&clickBeacon=&campaignid=&puidType=&contentcategory=&imgsize=&tsource=&pageid=&deviceid=&token=&contenttype=&subid=&impressionid=&campaigntype=&puid=&requestid=&contentkeyword=&subparam=' },
  ],
  carb: [
      { id: 1, name: '인스턴트오트',  icon: '🚀' , link: 'https://www.myprotein.co.kr/sports-nutrition/100-instant-oats/10529296.html?affil=awin&utm_content=https%3A%2F%2Fblog.naver.com%2Fhyun_do&utm_term=South+Korea&utm_source=AWin_504921&utm_medium=affiliate&utm_campaign=AffiliateWin&sv1=affiliate&sv_campaign_id=504921&awc=10751_1732444536_a1e014f631c14dc29138e44bd17c70b7' }
  ],
  vitamin: [
      { id: 1, name: '투퍼데이 캡슐(가성비 - 월 1만원)', icon: '🍀' , link : 'https://iherb.co/MgZTxLNh' },
      { id: 2, name: '투퍼데이 태블릿(가성비 - 월 1만원)', icon: '🍀' , link: 'https://iherb.co/5QWYd6um' },
      { id: 3, name: 'Thorne Research 종합비타민(월 3만원)', icon: '🍀' , link: 'https://iherb.co/5rEjoCz' },
  ],
  probiotic: [
      { id: 1, name: '캘리포니아골드 50억', icon: '🍀' , link: 'https://iherb.co/sGovQsd8' },
      { id: 2, name: '자로우 50억', icon: '🍀' , link: 'https://iherb.co/sCGskhux' },
  ],
  caffeine: [
      { id: 1, name: 'GAT 카페인', icon: '🍀' , link: 'https://iherb.co/ggZtfspX' },
      { id: 2, name: 'Teacrine(느린 카페인)', icon: '🚀' , link: 'https://coupa.ng/bWj8hd' },
      { id: 3, name: '퓨어 카페인 200mg', icon: '🎉' , link: 'http://bit.ly/2GpBfZo' },
  ],
  creatine: [
      { id: 1, name: '크레아틴(크레아퓨어)(추천제품)', icon: '🎉' , link: 'http://bit.ly/2vmdsUR' },
      { id: 2, name: '크레아틴 Now food', icon: '🚀' , link: 'https://coupa.ng/b0U1Dg' },
      { id: 3, name: '크레아틴 (AIIMAX)', icon: '🚀' , link: 'https://link.coupang.com/a/myLCm' },
  ],
  betaAlanine: [
      { id: 1, name: 'Now Foods 베타알라닌', icon: '🍀' , link: 'https://iherb.co/5NfxE2C' },
      { id: 2, name: '프리마포스 베타알라닌', icon: '🚀' , link: 'https://coupa.ng/bDWXMz' },
      { id: 3, name: '베타알라닌', icon: '🎉' , link: 'http://bit.ly/2UOZ4nt' },
      { id: 4, name: 'Now 베타알라닌', icon: '🚀' , link: 'https://coupa.ng/caJKdb' },
  ],
  vitaminD: [
      { id: 1, name: 'D3 2500, K2 80mcg', icon: '🍀' , link: 'https://iherb.co/jkZzFcFK' },
      { id: 2, name: 'D3 5000, K2 100mcg', icon: '🍀' , link: 'https://iherb.co/6rBBBiqg' },
      { id: 3, name: 'D3 5000', icon: '🍀' , link: 'https://iherb.co/uEU2o2sd' },
  ],
  calcium: [
      { id: 1, name: 'Calcium Citrate', icon: '🍀' , link: 'https://iherb.co/K7SppwhR' },
      { id: 2, name: '밀크프로틴', icon: '🎉' , link: 'http://bit.ly/31lFbTC' },
      { id: 3, name: '미셀라 카제인', icon: '🎉' , link: 'http://bit.ly/2GTIhql' },
  ],
  omega3: [
      { id: 1, name: '오메가3, 800mg, TG', icon: '🍀' , link: 'https://iherb.co/dD21ur4J' },
      { id: 2, name: '스포츠리서치, 오메가3, 937mg, TG', icon: '🍀' , link: 'https://iherb.co/eLD5Be3' },
      { id: 3, name: '스포츠리서치 오메가3 937mg TG', icon: '🚀' , link: 'https://coupa.ng/bDWYgW' },
  ]
  // 다른 카테고리의 제품들도 여기에 추가할 수 있습니다
};

const currentTab = ref('beginner');
const selectedCategory = ref('protein');

const filteredCategories = computed(() => {
  return categories[currentTab.value] || [];
});

const filteredProducts = computed(() => {
  return products[selectedCategory.value] || [];
});

const changeTab = (tabId) => {
  currentTab.value = tabId;
  selectedCategory.value = filteredCategories.value[0]?.id || null;
};

const selectCategory = (categoryId) => {
  selectedCategory.value = categoryId;
};
</script>

<style scoped>
.container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.category-tabs {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}

.tab-btn {
  padding: 10px 20px;
  border: none;
  background: none;
  cursor: pointer;
  font-size: 16px;
  color: #666;
}

.tab-btn.active {
  color: #000;
  font-weight: bold;
  border-bottom: 2px solid #87CEEB;
}

.user-level {
  display: flex;
  gap: 10px;
}

.level-btn {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 5px 10px;
  border: none;
  background-color: #f5f5f5;
  border-radius: 20px;
  cursor: pointer;
}

.content-wrapper {
  display: flex;
  gap: 20px;
}

.subcategories {
  width: 250px;
}

.subcategory-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 15px;
  margin-bottom: 10px;
  background-color: #f5f5f5;
  border-radius: 10px;
  cursor: pointer;
}

.subcategory-item.active {
  background-color: #e0f0ff;
}

.products-section {
  flex: 1;
}

.product-item {
  display: flex;
  align-items: center;
  padding: 15px;
  margin-bottom: 10px;
  background-color: #fff;
  border: 1px solid #eee;
  border-radius: 10px;
}

.product-info {
  flex: 1;
  margin-left: 10px;
}

.detail-btn {
  padding: 5px 10px;
  border: none;
  background: none;
  cursor: pointer;
}

.info-panel {
  background-color: #f8f9fa;
  padding: 20px;
  border-radius: 10px;
  margin-top: 20px;
}

.info-section {
  margin-bottom: 15px;
}

.message-box {
  display: flex;
  align-items: center;
  gap: 10px;
  background-color: #fff;
  padding: 15px;
  border-radius: 10px;
  margin-top: 20px;
}

.emoji {
  font-size: 24px;
}
</style>