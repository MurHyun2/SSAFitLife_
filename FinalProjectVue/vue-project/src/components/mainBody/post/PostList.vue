<template>
  <div class="list-container">
    <div class="board-header">
      <div class="header-row">
        <span class="header-title">제목</span>
        <span class="header-content">내용</span>
        <span class="header-writer">작성자</span>
        <span class="header-create">작성일</span>
        <span class="header-views">조회수</span>
      </div>
    </div>

    <div class="board-body">
      <div v-for="list in lists" :key="list.postNo">
        <RouterLink :to="{ name: 'postDetail', params: { postNo: list.postNo } }" custom>
          <template #default="{ navigate }">
            <div class="post-row" @click="navigate">
              <span class="post-title">{{ list.postTitle }}</span>
              <span class="post-content">{{ list.postContent }}</span>
              <span class="post-writer">{{ list.nickname }}</span>
              <span class="post-create">{{ list.postCreatedDate }}</span>
              <span class="post-views">{{ list.postViews }}</span>
            </div>
          </template>
        </RouterLink>
      </div>
    </div>
  </div>

  <div class="board-footer">
<!--    <div class="pagination">
      <button class="page-btn" @click="changePage(currentPage - 1)" :disabled="currentPage === 1">이전</button>
      <span class="page-text">페이지 {{ currentPage }} / {{ totalPages }}</span>
      <button class="page-btn" @click="changePage(currentPage + 1)" :disabled="currentPage === totalPages">다음</button>
    </div> -->

    <div class="button-container">
      <RouterLink class="regist-button" :to="{ name: 'postRegist'}">등록</RouterLink>
    </div>

    <div class="search-container">
      <select v-model="searchStandard" class="search-select">
        <option>제목</option>
        <option>내용</option>
        <option>작성자</option>
      </select>
      <input type="text" class="search-input" :placeholder="`검색할 ${searchStandard} 입력`"/>
      <button class="search-button">검색</button>
    </div>
  </div>
</template>

<script setup>
import {ref, watch} from 'vue';
import {useRoute} from 'vue-router';
import axiosInstance from "@/plugins/axios.js";

const route = useRoute();
const currentMenu = ref('');
const currentView = ref('');
const lists = ref([]);
const searchStandard = ref('제목');

const requestPostList = async () => {
  try {
    const {data} = await axiosInstance.get('http://localhost:8080/post/post');
    lists.value = data;
  } catch (error) {
    console.error('게시물 리스트를 불러오는데 실패했습니다:', error);
  }
};

watch(() => route.path, async (path) => {
      const [_, menu, view] = path.split('/');
      currentMenu.value = menu || '';
      currentView.value = view || '';
      if (menu === 'board') {
        await requestPostList();
      }
    },
    {immediate: true}
);
</script>

<style scoped>
.list-container {
  background-color: white;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin: 20px;
  overflow: hidden;
}

.board-header {
  background-color: #b1def0;
  padding: 15px 0;
}

.header-row, .post-row {
  display: flex;
  align-items: center;
  padding: 0 20px;
}

.header-row span, .post-row span {
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 18px;
}

.header-title, .post-title { flex: 2; }
.header-content, .post-content { flex: 3; }
.header-writer, .post-writer { flex: 1; }
.header-create, .post-create { flex: 1.5; }
.header-views, .post-views { flex: 1; }

.post-row {
  padding: 12px 20px;
  border-bottom: 1px solid #eee;
  cursor: pointer;
  transition: background-color 0.3s;
}

.post-row:hover {
  background-color: #f8f9fa;
}

.board-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  margin-top: 20px;
}

.pagination {
  display: flex;
  align-items: center;
  gap: 15px;
}

.page-btn {
  padding: 8px 16px;
  border: none;
  background-color: #b1def0;
  color: white;
  border-radius: 25px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.page-btn:hover:not(:disabled) {
  background-color: #97d4e9;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-text {
  font-size: 16px;
}

.regist-button {
  text-decoration: none;
  padding: 8px 20px;
  background-color: #b1def0;
  color: white;
  border-radius: 25px;
  transition: background-color 0.3s;
}

.regist-button:hover {
  background-color: #97d4e9;
}

.search-container {
  display: flex;
  gap: 10px;
}

.search-select {
  padding: 8px;
  border: 1px solid #b1def0;
  border-radius: 5px;
  background-color: white;
}

.search-input {
  padding: 8px 12px;
  border: 1px solid #b1def0;
  border-radius: 5px;
  width: 200px;
}

.search-button {
  padding: 8px 16px;
  background-color: #b1def0;
  color: white;
  border: none;
  border-radius: 25px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.search-button:hover {
  background-color: #97d4e9;
}
</style>