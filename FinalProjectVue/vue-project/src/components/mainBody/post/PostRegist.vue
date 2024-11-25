<template>
  <div class="regist-container">
    <h2>게시물 등록</h2>
    <div class="content-wrapper">
      <form @submit.prevent="registPost">
        <div class="form-group">
          <span class="label">제목</span>
          <input 
            class="input-field" 
            type="text" 
            v-model="newPostTitle" 
            placeholder="제목을 입력하세요"
          >
        </div>

        <div class="form-group">
          <span class="label">내용</span>
          <textarea 
            class="textarea-field" 
            v-model="newPostContent" 
            placeholder="내용을 입력하세요"
          ></textarea>
        </div>

        <div class="button-group">
          <button class="btn btn-submit" type="submit">등록</button>
          <RouterLink class="btn btn-list" :to="{ name: 'posts' }">목록</RouterLink>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import {ref} from 'vue';
import {useRouter} from 'vue-router';
import axiosInstance from "@/plugins/axios.js";

const router = useRouter();
const newPostTitle = ref('');
const newPostContent = ref('');
const userNo = ref('1');

const registPost = async () => {
  try {
    await axiosInstance.post(`http://localhost:8080/post/post/${userNo.value}`, {
      postTitle: newPostTitle.value,
      postContent: newPostContent.value,
    });
    alert('게시물이 성공적으로 등록되었습니다.');
    await router.push({name: 'posts'});
  } catch (error) {
    console.error('게시물 등록 실패:', error);
  }
};
</script>

<style scoped>
.regist-container {
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

.form-group {
  margin-bottom: 25px;
  display: flex;
  align-items: flex-start;
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
  display: inline-block;
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
  padding: 8px 20px;
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
  background-color: #b1def0;
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
  .form-group {
    flex-direction: column;
  }
  
  .label {
    margin-bottom: 10px;
  }
  
  .button-group {
    flex-direction: column;
  }
  
  .btn {
    width: 100%;
  }
}
</style>