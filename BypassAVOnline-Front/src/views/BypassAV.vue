<template>
  <div class="home">

    <div class="logo-section">
      <img src="@/assets/logo1.png" alt="Logo" />
    </div>
    <div class="compiler-container">
        <h1>在线免杀平台</h1>

        <div class="input-section">
          <el-input class="custom-input" v-model="code" :rows="5" type="textarea" placeholder="在这里输入Shellcode,例如 unsigned char Data[] = { 0xFC, 0x48, ....};"></el-input>
        </div>
        <div class="select-section">
          <el-select class="custom-input1" v-model="selectedLanguage" placeholder="选择免杀的编程语言" @change="resetSecondOption">
            <el-option label="nim" value="nim"></el-option>
            <el-option label="c" value="c"></el-option>
          </el-select>
          <el-select class="custom-input2" v-if="showSecondSelect" v-model="selectedTemplate" placeholder="选择免杀方式">
            <el-option v-if="selectedLanguage === 'nim'" label="Nim加载器 - 1" value="v1"></el-option>
            <el-option v-if="selectedLanguage === 'c'" label="C加载器 - 1" value="v1"></el-option>
          </el-select>
        </div>
        <div class="button-section">
          <el-button @click="handleCompile" :disabled="selectedLanguage === '' || selectedTemplate === ''">Compile</el-button>
        </div>


      <div class="result-section">
        <div class="button-section">
          <el-button @click="handleDownload" :disabled="compilationResult === ''">Download</el-button>
        </div>
      </div>

      </div>
    <el-footer class="footer">
      <p>&copy; 2023 在线免杀平台, powered by <a href="https://github.com/yutianqaq">Yutian</a></p>
    </el-footer>
  </div>
</template>

<script>
import {fetchCompile, fetchDownloadLink} from '../api/compile.js';
import { ElNotification } from 'element-plus'
const open1 = () => {
  ElNotification({
    title: 'Success',
    message: '生成中，请等待...',
    type: 'success',
  })
}
const open4 = () => {
  ElNotification({
    title: 'Error',
    message: '输入格式不正确，请检查格式',
    type: 'error',
  })
}
export default {
  name: 'BypassAV',
  data() {
    return {
      code: '',
      selectedTemplate: '',
      selectedLanguage: '',
      compilationResult: '',
      downloadLink: null,
      response: null
    };
  },
  computed: {
    showSecondSelect() {
      return this.selectedLanguage === 'nim' || this.selectedLanguage === 'c';
    }
  },
  methods: {

    async fetchCompileData(endpoint, data) {
      try {
        const response = await fetchCompile(endpoint, data);
        this.compilationResult = response.data.data.downloadLink;
      } catch (error) {
        this.compilationResult = `Error: ${error.message}`;
      }
    },
    handleCompile() {
      if (this.selectedLanguage === 'nim' || this.selectedLanguage === 'c') {
        const match = this.code.match(/\{([^}]+)\}/);
        let shellcode = '';
        if (match) {
          shellcode = match[1]; 
          console.log(shellcode);
          console.log(match); 
        } else {
          open4();
          return;
        }

        const cleanedCode = shellcode.replace(/(\r\n|\n|\r)/gm, '');
        this.fetchCompileData(`compile${this.selectedLanguage.toUpperCase()}`, {
          code: cleanedCode,
          templateName: this.selectedTemplate,
        });
      } else {
        this.compilationResult = 'Please select a valid language (nim or c).';
      }
    },
    async handlecompilationResult() {
      try {
        const response = await fetchDownloadLink(this.compilationResult);
        const contentDisposition = response.headers['content-disposition'];

        const blob = new Blob([response.data]);
        let filename = 'fail.bin'; // Default filename

        if (contentDisposition) {
          const matches = contentDisposition.match(/filename="(.+)"/);
          if (matches && matches.length > 1) {
            filename = matches[1];
          }
        }

        const url = URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = filename;

        document.body.appendChild(link);

        await new Promise(resolve => setTimeout(resolve, 100));

        link.click();
        document.body.removeChild(link);
        URL.revokeObjectURL(url);
        this.compilationResult = '';
      } catch (error) {
        console.error('Error fetching or processing download data:', error);
      }
    },
    handleDownload() {
      if (this.compilationResult) {
        this.handlecompilationResult();
      } else {
        console.warn('No compilation result available for download.');
      }
    },
    resetSecondOption() {
      this.selectedTemplate = '';
    },
    
  }
};
</script>

<style scoped>
.compiler-container {
  position: relative;
  max-width: 800px;
  margin: 0 auto;
  padding: 100px;
  border: 1px solid #ccc;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}

.logo-section {
  position: absolute;
  top: 120px;
  left: 120px;
}

.logo-section img {
  max-width: 65px;
}

.input-section {
  margin: 20px;
}
.custom-input {
  width: 480px; 
}
.custom-input1 {
  width: 480px; 
}
.custom-input2 {
  width: 480px; 
}
.select-section,
.button-section,
.result-section {
  margin-bottom: 40px;
}

.result-section pre {
  white-space: pre-wrap;
  font-size: 16px;
  line-height: 1.5;
}
</style>

