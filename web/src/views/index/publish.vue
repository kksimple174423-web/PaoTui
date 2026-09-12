<template>
  <div class="publish">
    <Header/>
    <div class="publish-content">
      <h2 class="page-title">发布跑腿任务</h2>
      <p class="page-tip">填写任务信息，骑手接单后即可为你跑腿</p>

      <div class="form-row">
        <label>任务标题 <i class="req">*</i></label>
        <input v-model="form.title" maxlength="50" placeholder="例如：代取快递：中通小件一件">
      </div>

      <div class="form-row">
        <label>任务类型 <i class="req">*</i></label>
        <select v-model="form.classificationId">
          <option value="">请选择任务类型</option>
          <option v-for="item in classificationData" :key="item.id" :value="item.id">{{ item.title }}</option>
        </select>
      </div>

      <div class="form-row">
        <label>悬赏金额（元） <i class="req">*</i></label>
        <input v-model="form.reward" type="number" min="0" placeholder="例如：5">
      </div>

      <div class="form-row">
        <label>取件地址 <i class="req">*</i></label>
        <input v-model="form.pickupAddress" placeholder="例如：菜鸟驿站(东门店)">
      </div>

      <div class="form-row">
        <label>送达地址 <i class="req">*</i></label>
        <input v-model="form.deliveryAddress" placeholder="例如：3号宿舍楼">
      </div>

      <div class="form-row">
        <label>期望送达时间</label>
        <input v-model="form.expectTime" placeholder="例如：今天 18:00 前">
      </div>

      <div class="form-row">
        <label>联系电话</label>
        <input v-model="form.contactPhone" placeholder="方便骑手联系你">
      </div>

      <div class="form-row">
        <label>物品规格</label>
        <input v-model="form.weight" placeholder="例如：小件 / 中号纸箱">
      </div>

      <div class="form-row">
        <label>任务标签</label>
        <div class="tag-list">
          <span class="tag" :class="{'tag-select': selectedTags.includes(item.id)}"
                v-for="item in tagData" :key="item.id" @click="toggleTag(item.id)">{{ item.title }}</span>
        </div>
      </div>

      <div class="form-row">
        <label>物品图片</label>
        <div class="upload-box">
          <input type="file" accept="image/*" @change="onFileChange">
          <img v-if="previewUrl" :src="previewUrl" class="preview">
        </div>
      </div>

      <div class="form-row column">
        <label>任务描述</label>
        <textarea v-model="form.description" rows="4" placeholder="补充取件码、注意事项等信息"></textarea>
      </div>

      <div class="form-row">
        <label>备注</label>
        <input v-model="form.remark" placeholder="选填">
      </div>

      <div class="submit-row">
        <button class="submit-btn" :disabled="submitting" @click="handleSubmit">
          {{ submitting ? '发布中...' : '发布任务' }}
        </button>
      </div>
    </div>
    <Footer/>
  </div>
</template>

<script setup>
import {message} from "ant-design-vue";
import Header from '/@/views/index/components/header.vue'
import Footer from '/@/views/index/components/footer.vue'
import {createApi} from '/@/api/task'
import {listApi as listClassificationList} from '/@/api/classification'
import {listApi as listTagList} from '/@/api/tag'
import {useUserStore} from "/@/store";
import {useRouter} from "vue-router/dist/vue-router";

const router = useRouter()
const userStore = useUserStore()

const form = reactive({
  title: '',
  classificationId: '',
  reward: '',
  pickupAddress: '',
  deliveryAddress: '',
  expectTime: '',
  contactPhone: '',
  weight: '',
  remark: '',
  description: ''
})

const classificationData = ref([])
const tagData = ref([])
const selectedTags = ref([])
const imageFile = ref(null)
const previewUrl = ref('')
const submitting = ref(false)

onMounted(() => {
  if (!userStore.user_id) {
    message.warn('请先登录')
    router.push({name: 'login'})
    return
  }
  listClassificationList().then(res => {
    classificationData.value = res.data
  })
  listTagList().then(res => {
    tagData.value = res.data
  })
})

const toggleTag = (id) => {
  const idx = selectedTags.value.indexOf(id)
  if (idx >= 0) {
    selectedTags.value.splice(idx, 1)
  } else {
    selectedTags.value.push(id)
  }
}

const onFileChange = (e) => {
  const file = e.target.files[0]
  if (file) {
    imageFile.value = file
    previewUrl.value = URL.createObjectURL(file)
  }
}

const handleSubmit = () => {
  if (!form.title) {
    message.warn('请填写任务标题')
    return
  }
  if (!form.classificationId) {
    message.warn('请选择任务类型')
    return
  }
  if (!form.reward) {
    message.warn('请填写悬赏金额')
    return
  }
  if (!form.pickupAddress || !form.deliveryAddress) {
    message.warn('请填写取件地址和送达地址')
    return
  }

  const formData = new FormData()
  formData.append('title', form.title)
  formData.append('classificationId', form.classificationId)
  formData.append('reward', form.reward)
  formData.append('pickupAddress', form.pickupAddress)
  formData.append('deliveryAddress', form.deliveryAddress)
  formData.append('expectTime', form.expectTime || '')
  formData.append('contactPhone', form.contactPhone || '')
  formData.append('weight', form.weight || '')
  formData.append('remark', form.remark || '')
  formData.append('description', form.description || '')
  formData.append('tags', selectedTags.value.join(','))
  if (imageFile.value) {
    formData.append('imageFile', imageFile.value)
  }

  submitting.value = true
  createApi(formData).then(res => {
    submitting.value = false
    message.success('任务发布成功')
    router.push({name: 'portal'})
  }).catch(err => {
    submitting.value = false
    message.error(err.msg || '发布失败')
  })
}
</script>

<style scoped lang="less">
.publish-content {
  width: 700px;
  margin: 40px auto 60px;
}

.page-title {
  font-size: 22px;
  color: #152844;
  font-weight: 600;
  margin-bottom: 6px;
}

.page-tip {
  color: #999;
  font-size: 14px;
  margin-bottom: 24px;
}

.form-row {
  display: flex;
  align-items: center;
  margin-bottom: 16px;

  label {
    flex: 0 0 130px;
    color: #333;
    font-size: 14px;

    .req {
      color: #f5222d;
      font-style: normal;
    }
  }

  input, select, textarea {
    flex: 1;
    height: 36px;
    line-height: 36px;
    border: 1px solid #cedce4;
    border-radius: 4px;
    padding: 0 10px;
    font-size: 14px;
    outline: none;
    background: #fff;
  }

  textarea {
    height: 96px;
    line-height: 22px;
    padding: 8px 10px;
    resize: vertical;
  }

  &.column {
    align-items: flex-start;
  }
}

.tag-list {
  flex: 1;

  .tag {
    display: inline-block;
    border: 1px solid #a1adc6;
    border-radius: 16px;
    height: 24px;
    line-height: 22px;
    padding: 0 12px;
    margin: 0 8px 8px 0;
    font-size: 12px;
    color: #152833;
    cursor: pointer;
  }

  .tag-select {
    background: #4684e2;
    color: #fff;
    border-color: #4684e2;
  }
}

.upload-box {
  flex: 1;

  input {
    border: none;
    padding: 0;
    height: auto;
  }

  .preview {
    display: block;
    width: 160px;
    height: 120px;
    object-fit: cover;
    margin-top: 10px;
    border-radius: 4px;
  }
}

.submit-row {
  margin-top: 28px;
  text-align: center;
}

.submit-btn {
  background: #4684e2;
  color: #fff;
  border: none;
  border-radius: 4px;
  height: 40px;
  line-height: 40px;
  padding: 0 48px;
  font-size: 15px;
  cursor: pointer;

  &:disabled {
    background: #a8c1e8;
    cursor: not-allowed;
  }
}
</style>