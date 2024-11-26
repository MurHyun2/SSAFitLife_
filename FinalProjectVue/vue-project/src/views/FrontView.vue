<template>
  <div>
    <nav class="container">
      <div v-for="menu in menus" :key="menu.index" class="menu">
        <h2 v-if="menu.label === '체중 관리'" class="SSAFitLife">SSAFitLife</h2>
        <h1 class="title">{{ menu.label }}</h1>
        <div class="views">
          <div v-for="view in menu.views" class="view">
            <RouterLink :to="`/${menu.basePath}/${view.view}`" class="link">{{ view.label }}</RouterLink>
            <a class="arrow">></a>
          </div>
        </div>
      </div>
    </nav>
  </div>
</template>

<script setup>
const menus = [
  {
    label: '체중 관리',
    basePath: 'weight-manage',
    views: [
      {label: '체중 관리', view: 'weight-manage'},
      // {label: '체조성 기록', view: 'body-record'},
      {label: '활동 관리', view: 'activity-manage'}
    ]
  },
  {
    label: '식단 관리',
    basePath: 'diet-manage',
    views: [
      {label: '식단 짜기', view: 'diet-plan'},
      // {label: '밀프렙 도우미', view: 'millprep-assist'},
      // {label: '보충제 활용', view: 'sup-use'}
    ]
  },
  {
    label: '게시판',
    basePath: 'board',
    views: [
      {label: '게시판 이동', view: 'posts'},
    ]
  },
  {
    label: '제품 추천',
    basePath: 'product-recommend',
    views: [
      {label: '보충제 추천', view: 'sup'},
      {label: '닭가슴살 추천', view: 'chicken-breast'},
    ]
  },
]

</script>

<style scoped>
.SSAFitLife {
  margin-top: 25px;
  font-size: 28px;
  color: #97d4e9;
  margin-left: 25px;
}

.title {
  position: absolute;
  margin-left: 25px;
  font-size: 28px;
  top: 100px;
}

.views {
  /* background-color: #05202b; */

  width: 100%;
  position: absolute;
  top: 200px;
}

.view {
  margin-top: 20px;
}

.link {
  margin-left: 25px;
  font-size: 25px;
  text-decoration: none; /* 링크 밑줄 없애기 */
  color: gray; /* 색상 설정 */
}

.arrow {
  color: gray;
  font-size: 23px;
  position: absolute;
  font-weight: bold;
  right: 25px;
}

.container :hover {
  color: white;
  background-color: #a6daec;
  transition: 0.2s;
}

.container :hover.SSAFitLife {
  color: white;
  transition: 0.2s;
}

.container :hover .link {
  color: white;
  transition: 0.2s;
  font-weight: bold;
}

.container :hover .arrow {
  color: white;
  transition: 0.2s;
}

.container :hover .SSAFitLife {
  color: white;
  transition: 0.2s;
}

.menu {
  /* margin-left: 20px; */
  background-color: #e5f2fb;
  height: 98vh;
  width: 100vh;
  margin: 8px;
  margin-left: 20px;
  border-radius: 30px;
  position: relative;
}

.container {
  position: relative;
  /* background-color: #05202b; */
  display: flex;
  /* flex-direction: row; */
  justify-content: center;
  align-items: center;
  height: 98vh;
  /* width: 100vh; */
}
</style>
