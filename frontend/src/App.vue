<template>
  <div class="app">
    <h1>人脸识别系统</h1>
    
    <!-- 人脸库管理 -->
    <div class="section">
      <h2>人脸库管理</h2>
      <div class="upload-area" @drop="handleDrop" @dragover.prevent>
        <p>点击或拖拽图片到此处上传</p>
        <input type="file" multiple accept="image/*" @change="handleFileSelect" />
      </div>
      <div v-if="uploadedFiles.length > 0" class="file-list">
        <h3>已选择的文件：</h3>
        <ul>
          <li v-for="(file, index) in uploadedFiles" :key="index">
            {{ file.name }}
            <button @click="removeFile(index)">移除</button>
          </li>
        </ul>
        <button @click="uploadToServer" class="upload-btn">上传到服务器</button>
      </div>
    </div>
    
    <!-- 人脸识别搜索 -->
    <div class="section">
      <h2>人脸识别搜索</h2>
      <div class="upload-area" @drop="handleSearchDrop" @dragover.prevent>
        <p>点击或拖拽一张包含人脸的图片</p>
        <input type="file" accept="image/*" @change="handleSearchFileSelect" />
      </div>
      <div v-if="searchFile" class="search-preview">
        <img :src="searchFileUrl" alt="搜索图片" />
        <button @click="startSearch" class="search-btn">开始搜索</button>
      </div>
      
      <!-- 搜索结果 -->
      <div v-if="searchResults.length > 0" class="search-results">
        <h3>搜索结果：</h3>
        <div class="result-list">
          <div v-for="(result, index) in searchResults" :key="index" class="result-item">
            <img :src="result.imageUrl" alt="匹配图片" />
            <div class="result-info">
              <p>相似度：{{ result.similarity }}%</p>
              <p>文件：{{ result.fileName }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'App',
  data() {
    return {
      uploadedFiles: [],
      searchFile: null,
      searchFileUrl: '',
      searchResults: []
    }
  },
  methods: {
    // 处理文件选择
    handleFileSelect(event) {
      const files = Array.from(event.target.files)
      this.uploadedFiles = [...this.uploadedFiles, ...files]
    },
    
    // 处理拖拽上传
    handleDrop(event) {
      event.preventDefault()
      const files = Array.from(event.dataTransfer.files)
      this.uploadedFiles = [...this.uploadedFiles, ...files]
    },
    
    // 移除文件
    removeFile(index) {
      this.uploadedFiles.splice(index, 1)
    },
    
    // 上传到服务器
    async uploadToServer() {
      const formData = new FormData()
      this.uploadedFiles.forEach(file => {
        formData.append('files', file)
      })
      
      try {
        const response = await fetch('/api/face/upload', {
          method: 'POST',
          body: formData
        })
        
        if (response.ok) {
          alert('上传成功！')
          this.uploadedFiles = []
        } else {
          alert('上传失败，请重试')
        }
      } catch (error) {
        console.error('上传错误:', error)
        alert('上传失败，请检查网络连接')
      }
    },
    
    // 处理搜索文件选择
    handleSearchFileSelect(event) {
      this.searchFile = event.target.files[0]
      this.searchFileUrl = URL.createObjectURL(this.searchFile)
    },
    
    // 处理搜索拖拽上传
    handleSearchDrop(event) {
      event.preventDefault()
      this.searchFile = event.dataTransfer.files[0]
      this.searchFileUrl = URL.createObjectURL(this.searchFile)
    },
    
    // 开始搜索
    async startSearch() {
      if (!this.searchFile) {
        alert('请先选择一张图片')
        return
      }
      
      const formData = new FormData()
      formData.append('file', this.searchFile)
      
      try {
        const response = await fetch('/api/face/search', {
          method: 'POST',
          body: formData
        })
        
        if (response.ok) {
          this.searchResults = await response.json()
        } else {
          alert('搜索失败，请重试')
        }
      } catch (error) {
        console.error('搜索错误:', error)
        alert('搜索失败，请检查网络连接')
      }
    }
  }
}
</script>

<style scoped>
.app {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  font-family: Arial, sans-serif;
}

h1 {
  text-align: center;
  color: #333;
}

.section {
  margin: 40px 0;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background-color: #f9f9f9;
}

h2 {
  color: #555;
  margin-bottom: 20px;
}

.upload-area {
  border: 2px dashed #ddd;
  border-radius: 8px;
  padding: 40px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.upload-area:hover {
  border-color: #4CAF50;
  background-color: #f0f8f0;
}

.upload-area input[type="file"] {
  margin-top: 20px;
}

.file-list {
  margin-top: 20px;
}

.file-list ul {
  list-style: none;
  padding: 0;
}

.file-list li {
  display: flex;
  justify-content: space-between;
  padding: 10px;
  border-bottom: 1px solid #eee;
}

.upload-btn, .search-btn {
  margin-top: 20px;
  padding: 10px 20px;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
}

.upload-btn:hover, .search-btn:hover {
  background-color: #45a049;
}

.search-preview {
  margin-top: 20px;
  text-align: center;
}

.search-preview img {
  max-width: 300px;
  max-height: 300px;
  margin-bottom: 20px;
  border-radius: 8px;
}

.search-results {
  margin-top: 30px;
}

.result-list {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.result-item {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 10px;
  width: 200px;
  text-align: center;
  background-color: white;
}

.result-item img {
  max-width: 180px;
  max-height: 180px;
  border-radius: 4px;
  margin-bottom: 10px;
}

.result-info {
  font-size: 14px;
  color: #666;
}
</style>