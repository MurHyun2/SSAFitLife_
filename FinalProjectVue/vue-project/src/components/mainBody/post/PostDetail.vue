<template>
  <div>
    <h2>상세 게시물</h2>
    <div class="container">
      <span class="title">
        <span class="item">제목</span>
        <span class="body">{{ list.postTitle }}</span>
      </span>
      <span class="create">
        <span class="item">등록일</span>
        <span class="body">{{ list.postCreatedDate }}</span>
      </span>
    </div>
    <div class="container">

      <span class="writer">
        <span class="item">작성자</span>
        <span class="body">{{ list.memNo }}</span>
      </span>
      <span class="update">
        <span class="item">수정일</span>
        <span class="body">{{ list.postUpdatedDate }}</span>
      </span>
    </div>

    <div class="container">
      <span class="views">
        <span class="item">조회수</span>
        <span class="body">{{ list.postViews }}</span>
      </span>
    </div>

    <div class="container">
      <span class="content">
        <span class="item">내용</span>
        <span class="body">{{ list.postContent }}</span>
      </span>
    </div>

    <div class="buttons">
      <RouterLink class="update-button" :to="{ name: 'postUpdate'}">수정</RouterLink>
      <RouterLink class="delete-button" :to="{name: 'posts'}" @click="requestPostDelete">삭제</RouterLink>
      <RouterLink class="post-button" :to="{name: 'posts'}">목록</RouterLink>
    </div>

  </div>
</template>

<script setup>
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
  const {data} = await axiosInstance.get(`http://localhost:8080/post/post/${postNo.value}`);
  list.value = data;
}

const requestPostDelete = async () => {
  await axiosInstance.delete(`http://localhost:8080/post/post/${postNo.value}`);
  window.location.reload();
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
.buttons {

  display: flex;
  margin-left: auto;
  margin-right: 30%;
  justify-content: flex-end;
}

.update-button, .delete-button, .post-button {
  text-decoration: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 1px 1px 5px rgba(0, 0, 0, 0.3);
  color: white;
  background-color: #97d4e9;
  border-radius: 35px;
  width: 64px;
  height: 27px;
  margin: 10px;

  /* flex-shrink: 0; */
}

.update-button {
  color: blue;
}

.delete-button {
  color: red;
}

.post-button {
  color: gray;
}

.container {
  position: relative;
  display: flex;
  align-items: center;
}

.container span {
  margin: 8px;
  display: flex;
}

.item {
  display: flex;
  flex-shrink: 0;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  color: white;
  background-color: #97d4e9;
  border-radius: 35px;
  width: 64px;
  height: 27px;
}

.title {
  flex: 1;
}

.writer {
  flex: 1;
}

.create {
  flex: 1;
}

.update {
  flex: 1;
}

.body {
  display: flex;
  flex-shrink: 0;
}

h2 {
  display: flex;
  justify-content: center;
  margin-bottom: 60px;
}
</style>