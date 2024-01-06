<template>
  <div class="home">
    <h1>在线免杀生成平台</h1>

    <div class="logo-section">
      <img src="@/assets/logo1.png" alt="Logo" />
    </div>
    <div class="compiler-container">
      <div class="button-section">
        <el-button @click="showElement1">bin 格式</el-button>
        <el-button @click="showElement2">数组格式</el-button>
        <el-button @click="showElement3">免责声明</el-button>
      </div>
      <div class="button-section">
      </div>
      <div v-if="elementToShow === 1">
        <el-upload class="upload-demo" ref="uploadRef" action="" :limit="1" drag :auto-upload="false" :show-file-list="true" method="post"
          :multiple="true" :http-request="handleUpload">
          <el-icon class="el-icon--upload"><upload-filled /></el-icon>
          <div class="el-upload__text">
            将 bin 文件拖拽或 <em>点击这里上传</em>
          </div>
          <template #tip>
            <div class="el-upload__tip">
              bin 限制 1 mb
            </div>
          </template>
        </el-upload>
        <div class="select-section">
          <el-select class="custom-input1" v-model="selectedLanguage" placeholder="选择编程语言" @change="resetSecondOption">
            <el-option label="nim" value="nim"></el-option>
            <el-option label="c" value="c"></el-option>
          </el-select>
          <el-select class="custom-input2" v-if="showSecondSelect" v-model="selectedTemplate" placeholder="选择免杀方式">
            <el-option v-if="selectedLanguage === 'nim'" label="Nim加载器 - x2Ldr" value="v2"></el-option>
            <el-option v-if="selectedLanguage === 'nim'" label="Nim加载器 - Test" value="v1"></el-option>
            <el-option v-if="selectedLanguage === 'c'" label="C加载器 - Test" value="v1"></el-option>
          </el-select>
        </div>
        <el-button class="button-section" @click="submitUpload" 
        :disabled="(selectedLanguage === '' || selectedTemplate === '')">Compile
        </el-button>
      </div>

      <div v-if="elementToShow === 2">
        <div class="input-section">
          <el-input class="custom-input" v-model="code" :rows="5" type="textarea"
            placeholder="在这里输入Shellcode,例如 unsigned char Data[] = { 0xFC, 0x48, ....};"></el-input>
        </div>
        <div class="select-section">
          <el-select class="custom-input1" v-model="selectedLanguage" placeholder="选择编程语言" @change="resetSecondOption">
            <el-option label="nim" value="nim"></el-option>
            <el-option label="c" value="c"></el-option>
          </el-select>
          <el-select class="custom-input2" v-if="showSecondSelect" v-model="selectedTemplate" placeholder="选择免杀方式">
            <el-option v-if="selectedLanguage === 'nim'" label="Nim加载器 - x2Ldr" value="v2"></el-option>
            <el-option v-if="selectedLanguage === 'nim'" label="Nim加载器 - Test" value="v1"></el-option>
            <el-option v-if="selectedLanguage === 'c'" label="C加载器 - Test" value="v1"></el-option>
          </el-select>
        </div>
        <el-button class="button-section" @click="handleCompile"
        :disabled="!(selectedLanguage === '' || selectedTemplate !== '')">Compile</el-button>
      </div>

      <div v-if="elementToShow === 3">
        <p>
          本工具仅供安全研究和教学目的使用，用户须自行承担因使用该工具而引起的一切法律及相关责任。作者概不对任何法律责任承担责任，且保留随时中止、修改或终止本工具的权利。使用者应当遵循当地法律法规，并理解并同意本声明的所有内容。
        </p>  
      </div>



      <div class="result-section">
        <el-button class="button-section" @click="handleDownload"
          :disabled="compilationResult === ''">Download</el-button>
      </div>

    </div>
    <el-footer class="footer">
      <p>&copy; 2023 在线免杀平台, powered by <a href="https://github.com/yutianqaq">Yutian</a></p>
    </el-footer>
  </div>
</template>

<script>
import { fetchCompile, fetchDownloadLink, fetchCompileUpload } from '../api/compile.js';
import { ElNotification, ElUpload, } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue';
import { ref } from 'vue'

const open1 = (msg) => {
  ElNotification({
    title: 'Success',
    message: msg,
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
      response: null,
      elementToShow: 1,
      extraParams: {
        templateName: this.selectedTemplate
      },
    };
  },
  setup() {
    const uploadRef = ref(null);

    const submitUpload = () => {
      open1('生成中，请等待下载按钮亮起');
      uploadRef.value.submit();
    };

    return {
      uploadRef,
      submitUpload,
    };
  },
  computed: {
    showSecondSelect() {
      return this.selectedLanguage === 'nim' || this.selectedLanguage === 'c';
    }
  },
  methods: {
    showElement1() {
      this.elementToShow = 1;
      this.resetAllOption();
    },
    showElement2() {
      this.elementToShow = 2;
      this.resetAllOption();
    },
    showElement3() {
      this.elementToShow = 3;
      this.resetAllOption();
    },
    async fetchCompileData(endpoint, data) {
      try {
        const response = await fetchCompile(endpoint, data);
        this.compilationResult = response.data.data.downloadLink;
        open1('生成完成...');
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
        } else {
          open4();
          return;
        }

        const cleanedCode = shellcode.replace(/(\r\n|\n|\r)/gm, '');
        open1('生成中，请等待下载按钮亮起');
        this.fetchCompileData(`compile${this.selectedLanguage.toUpperCase()}`, {
          code: cleanedCode,
          templateName: this.selectedTemplate,
        });
      } else {
        this.compilationResult = 'Please select a valid language (nim or c).';
      }
    },
    async handleUpload(file) {
      var { file } = file;

      if (this.selectedLanguage === 'nim' || this.selectedLanguage === 'c') {
        const formData = new FormData();
        formData.append('file', file);
        formData.append('templateName', this.selectedTemplate);
        console.log(formData);
        try {
          const response = await fetchCompileUpload(`/upload/compile${this.selectedLanguage.toUpperCase()}`, formData, {
            headers: {
              'Content-Type': 'multipart/form-data',
            },
          });

          this.compilationResult = response.data.data.downloadLink;
          open1('生成完成...');
        } catch (error) {
          console.error('Error:', error);
        }
        this.resetAllOption();
        return true;
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
        history.go(0)
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
    resetAllOption() {
      this.selectedTemplate = '';
      this.selectedLanguage = '';
    }

  }
};
</script>

<style scoped>
.compiler-container {
  position: relative;
  max-width: 800px;
  width: 800px;
  height: 450px;
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
  text-align: center;
  width: 480px;
}

.custom-input2 {
  margin: 10px;
  width: 480px;
}

.select-section {
  margin: 10px;
}
.button-section{
  margin: 10px;
}
.result-section {
  margin-bottom: 40px;
}

.result-section pre {
  white-space: pre-wrap;
  font-size: 16px;
  line-height: 1.5;
}
</style>

