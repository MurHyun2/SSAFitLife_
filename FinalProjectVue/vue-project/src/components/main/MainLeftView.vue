<template>
  <div class="container">
    <div class="SSAFit">SSAFitLife</div>

    <RouterLink
        v-for="menu in menus"
        :key="menu.to"
        :class="{ select: isActive(menu.to) }"
        class="link"
        :to="menu.to"
    >
      <img :src="menu.src" :alt="menu.alt"/>
      <br/>
      {{ menu.label }}
    </RouterLink>
  </div>
</template>

<script setup>
import {useRoute} from 'vue-router';

const route = useRoute();

// 현재 라우트가 활성화된 메뉴를 판단하는 함수
const isActive = (menuTo) => {
  // 현재 라우트 경로와 menuTo 경로가 일치하는지 확인
  return route.path === menuTo || route.path.startsWith(menuTo + '/');
};

const menus = [
  {
    label: '홈',
    to: '/',
    src: '/homeButton.png',
    alt: 'Home',
  },
  {
    label: '체중관리',
    to: '/weight-manage/weight-manage',
    src: '/weightMangeButton.png',
    alt: 'WeightMange',
  },
  {
    label: '식단관리',
    to: '/diet-manage/diet-plan',
    src: '/dietMangeButton.png',
    alt: 'DietMange',
  },
  {
    label: '게시판',
    to: '/board/posts',
    src: '/PostButton.png',
    alt: 'Post',
  },
  {
    label: '제품추천',
    to: '/product-recommend/sup',
    src: '/productRecommendButton.png',
    alt: 'ProductRecommend',
  },
];
</script>

<style scoped>
.container {
  color: white;
  font-weight: bold;
  display: flex;
  flex-direction: column; /* 자식 요소를 세로로 정렬 */
  align-items: center; /* 자식 요소 수평 중앙 정렬 */
  justify-content: flex-start; /* 자식 요소 위쪽 정렬 */
}

.SSAFit {
  font-size: 25px;
  margin-top: 40px;
  margin-bottom: 20px;
}


.link {
  text-decoration: none;
  color: white;
  margin: 10px;
  width: 125px;
  height: 125px;
  border-radius: 25px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.link:hover {
  background-color: #b1def0;
  transition: 0.4s;
}

.select {
  background-color: #b1def0;
}
</style>
