<template>
  <div class="detail-container">
    <h2>상세 게시물</h2>
    <div class="content-wrapper">
      <div class="info-row">
        <span class="info-group">
          <span class="label">제목</span>
          <span class="value">{{ list.postTitle }}</span>
        </span>
        <span class="info-group">
          <span class="label">등록일</span>
          <span class="value">{{ list.postCreatedDate }}</span>
        </span>
      </div>

      <div class="info-row">
        <span class="info-group">
          <span class="label">작성자</span>
          <span class="value">{{ list.nickname }}</span>
        </span>
        <span class="info-group">
          <span class="label">수정일</span>
          <span class="value">{{ list.postUpdatedDate }}</span>
        </span>
      </div>

      <div class="info-row">
        <span class="info-group">
          <span class="label">조회수</span>
          <span class="value">{{ list.postViews }}</span>
        </span>
      </div>

      <div class="content-box">
        <span class="label">내용</span>
        <div class="content-value">{{ list.postContent }}</div>
      </div>

      <div class="button-group">
        <template v-if="list.isWriter">
          <RouterLink class="btn btn-update" :to="{ name: 'postUpdate'}">수정</RouterLink>
          <RouterLink class="btn btn-delete" :to="{name: 'posts'}" @click="requestPostDelete">삭제</RouterLink>
        </template>
        <RouterLink class="btn btn-list" :to="{name: 'posts'}">목록</RouterLink>
      </div>
    </div>
  </div>
</template>

<script setup>
// 스크립트 부분은 동일하게 유지
import {ref, watch} from 'vue';
import {useRoute} from 'vue-router';
import axiosInstance from "@/plugins/axios.js";

const route = useRoute()
const currentMenu = ref('');
const currentView = ref('');
const postNo = ref(route.params.postNo)
const list = ref({});
const commentList = ref({});

const requestPostDetail = async () => {
  const {data} = await axiosInstance.get(`/post/post/${postNo.value}`);
  list.value = data;
}

const requestPostDelete = async () => {
  const isConfirmed = confirm('정말 삭제하시겠습니까?');

  if (isConfirmed) {
    try {
      await axiosInstance.delete(`/post/post/${postNo.value}`);
      window.location.reload();
    } catch (error) {
      console.error('삭제 실패:', error);
      alert('삭제에 실패했습니다.');
    }
  }
}

watch(() => route.path, async (path) => {
      const [_, menu, view, postId] = path.split('/');
      currentMenu.value = menu || '';
      currentView.value = view || '';
      postNo.value = postId || '';

      if (postNo.value) {
        await requestPostDetail(postNo.value);
      }
    },
    {immediate: true}
);
</script>

<style scoped>
.detail-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

h2 {
  text-align: center;
  color: #333;
  margin-bottom: 40px;
  font-size: 24px;
}

.content-wrapper {
  background: white;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 30px;
}

.info-row {
  display: flex;
  margin-bottom: 20px;
  gap: 20px;
}

.info-group {
  display: flex;
  align-items: center;
  flex: 1;
}

.label {
  background-color: #b1def0;
  color: white;
  padding: 8px 16px;
  border-radius: 25px;
  font-weight: bold;
  font-size: 14px;
  min-width: 80px;
  text-align: center;
  margin-right: 15px;
}

.value {
  flex: 1;
  padding: 8px;
  color: #495057;
}

.content-box {
  margin-top: 30px;
}

.content-value {
  margin-top: 15px;
  padding: 20px;
  min-height: 200px;
  background: #f8f9fa;
  border-radius: 8px;
  color: #495057;
  line-height: 1.6;
}

.button-group {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 30px;
}

.btn {
  text-decoration: none;
  padding: 8px 20px;
  border-radius: 25px;
  font-weight: bold;
  transition: all 0.3s ease;
  min-width: 80px;
  text-align: center;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.btn-update {
  background-color: #b1def0;
  color: white;
}

.btn-delete {
  background-color: #ff6b6b;
  color: white;
}

.btn-list {
  background-color: #adb5bd;
  color: white;
}

.btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}
</style>