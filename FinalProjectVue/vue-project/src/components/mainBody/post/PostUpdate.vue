<template>
  <div class="update-container">
    <h2>게시물 수정</h2>
    <div class="content-wrapper">
      <form @submit.prevent="updatePost">
        <div class="info-section">
          <div class="info-row">
            <div class="form-group">
              <span class="label">제목</span>
              <input
                  class="input-field"
                  type="text"
                  v-model="list.postTitle"
                  :placeholder="list.postTitle"
              >
            </div>
            <div class="info-group">
              <span class="label">등록일</span>
              <span class="value">{{ list.postCreatedDate }}</span>
            </div>
          </div>

          <div class="info-row">
            <div class="info-group">
              <span class="label">작성자</span>
              <span class="value">{{ list.memNo }}</span>
            </div>
            <div class="info-group">
              <span class="label">수정일</span>
              <span class="value">{{ list.postUpdatedDate }}</span>
            </div>
          </div>

          <div class="info-row">
            <div class="info-group">
              <span class="label">조회수</span>
              <span class="value">{{ list.postViews }}</span>
            </div>
          </div>
        </div>

        <div class="form-group content-group">
          <span class="label">내용</span>
          <textarea
              class="textarea-field"
              v-model="list.postContent"
              :placeholder="list.postContent"
          ></textarea>
        </div>

        <div class="button-group">
          <button class="btn btn-submit" type="submit">수정</button>
          <RouterLink class="btn btn-cancel" :to="{ name: 'postDetail', params: { postNo: list.postNo } }">
            취소
          </RouterLink>
          <RouterLink class="btn btn-list" :to="{ name: 'posts' }">목록</RouterLink>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import {ref, watch, onMounted} from 'vue';
import {useRoute, useRouter} from 'vue-router';
import axiosInstance from "@/plugins/axios.js";

const route = useRoute();
const router = useRouter();
const postNo = ref(route.params.postNo);
const list = ref({});

const requestPostDetail = async () => {
  const {data} = await axiosInstance.get(`/post/post/${postNo.value}`);
  list.value = data;
};

const updatePost = async () => {
  try {
    await axiosInstance.put(`/post/post/${postNo.value}`, {
      postTitle: list.value.postTitle,
      postContent: list.value.postContent,
    });
    alert('게시물이 성공적으로 수정되었습니다.');
    router.push({name: 'postDetail', params: {postNo: postNo.value}});
  } catch (error) {
    console.error('게시물 수정에 실패했습니다:', error);
  }
};

onMounted(() => {
  requestPostDetail();
});

watch(() => route.params.postNo, (newPostNo) => {
  postNo.value = newPostNo;
  requestPostDetail();
}, {immediate: true});
</script>

<style scoped>
.update-container {
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

.info-section {
  margin-bottom: 30px;
}

.info-row {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.form-group {
  flex: 1;
  display: flex;
  align-items: center;
}

.info-group {
  flex: 1;
  display: flex;
  align-items: center;
}

.content-group {
  align-items: flex-start;
}

.label {
  background-color: #93c5fd;
  color: white;
  padding: 10px 3px;
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
}

.input-field {
  flex: 1;
  padding: 10px 15px;
  border: 1px solid #dee2e6;
  border-radius: 5px;
  font-size: 16px;
  transition: border-color 0.3s ease;
}

.textarea-field {
  flex: 1;
  padding: 15px;
  border: 1px solid #dee2e6;
  border-radius: 5px;
  min-height: 300px;
  font-size: 16px;
  resize: vertical;
  transition: border-color 0.3s ease;
}

.input-field:focus,
.textarea-field:focus {
  outline: none;
  border-color: #b1def0;
}

.button-group {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 30px;
}

.btn {
  text-decoration: none;
  padding: 10px 3px;
  border-radius: 25px;
  font-weight: bold;
  transition: all 0.3s ease;
  min-width: 80px;
  text-align: center;
  border: none;
  cursor: pointer;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.btn-submit {
  background-color: #93c5fd;
  color: white;
}

.btn-cancel {
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

@media (max-width: 768px) {
  .info-row {
    flex-direction: column;
    gap: 10px;
  }

  .form-group,
  .info-group {
    width: 100%;
  }

  .button-group {
    flex-direction: column;
  }

  .btn {
    width: 100%;
  }
}
</style>