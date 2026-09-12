<template>
  <div class="content-list">
    <div class="list-title">骑手认证</div>

    <div class="status-card" :class="statusClass">
      <div class="status-title">{{ statusText }}</div>
      <div class="status-desc">{{ statusDesc }}</div>
    </div>

    <!-- 已提交的资料 -->
    <div class="apply-info" v-if="applyData && applyData.id">
      <div class="info-row"><span class="label">真实姓名</span>{{ applyData.realName }}</div>
      <div class="info-row"><span class="label">学号</span>{{ applyData.studentNo }}</div>
      <div class="info-row"><span class="label">院系</span>{{ applyData.college }}</div>
      <div class="info-row"><span class="label">联系电话</span>{{ applyData.mobile }}</div>
      <div class="info-row"><span class="label">服务区域</span>{{ applyData.serviceArea }}</div>
      <div class="info-row"><span class="label">服务时段</span>{{ applyData.serviceTime }}</div>
      <div class="info-row"><span class="label">申请时间</span>{{ getFormatTime(applyData.createTime, true) }}</div>
      <div class="info-row" v-if="applyData.auditRemark">
        <span class="label">审核意见</span><span class="reject-text">{{ applyData.auditRemark }}</span>
      </div>
      <div class="info-row" v-if="applyData.cardImage">
        <span class="label">证件照</span>
        <img :src="cardUrl" class="card-img">
      </div>
    </div>

    <!-- 提交表单 -->
    <div class="verify-form" v-if="canApply">
      <div class="form-row">
        <label>真实姓名 <i class="req">*</i></label>
        <input v-model="form.realName" placeholder="请输入真实姓名">
      </div>
      <div class="form-row">
        <label>学号 <i class="req">*</i></label>
        <input v-model="form.studentNo" placeholder="请输入学号">
      </div>
      <div class="form-row">
        <label>院系</label>
        <input v-model="form.college" placeholder="例如：电子信息与计算机工程系">
      </div>
      <div class="form-row">
        <label>联系电话 <i class="req">*</i></label>
        <input v-model="form.mobile" placeholder="请输入手机号">
      </div>
      <div class="form-row">
        <label>服务区域</label>
        <input v-model="form.serviceArea" placeholder="例如：东区宿舍群、图书馆">
      </div>
      <div class="form-row">
        <label>服务时段</label>
        <input v-model="form.serviceTime" placeholder="例如：周一至周五 18:00-22:00">
      </div>
      <div class="form-row">
        <label>学生证照片</label>
        <input type="file" accept="image/*" @change="onFileChange">
      </div>
      <div class="submit-row">
        <button class="submit-btn" :disabled="submitting" @click="handleSubmit">
          {{ submitting ? '提交中...' : '提交认证申请' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import {message} from "ant-design-vue";
import {applyApi, myApplyApi} from '/@/api/runnerVerify'
import {getFormatTime} from '/@/utils/'
import {BASE_URL} from "/@/store/constants";
import {useUserStore} from "/@/store";

const userStore = useUserStore();

const STATUS_TEXT = {'0': '未申请', '1': '审核中', '2': '已认证', '3': '已驳回'}

const applyData = ref(null)
const form = reactive({
  realName: '',
  studentNo: '',
  college: '',
  mobile: '',
  serviceArea: '',
  serviceTime: ''
})
const cardFile = ref(null)
const submitting = ref(false)

onMounted(() => {
  getMyApply()
})

const statusText = computed(() => {
  const s = applyData.value && applyData.value.id ? applyData.value.status : '0'
  if (s === '0') return '未申请骑手认证'
  if (s === '1') return '认证审核中'
  if (s === '2') return '已通过骑手认证'
  return '认证未通过'
})

const statusDesc = computed(() => {
  const s = applyData.value && applyData.value.id ? applyData.value.status : '0'
  if (s === '0') return '提交认证资料后，管理员审核通过即可在任务大厅接单'
  if (s === '1') return '管理员正在审核你的资料，请耐心等待'
  if (s === '2') return '你可以前往骑手工作台接单赚取赏金了'
  return '很遗憾本次认证未通过，你可以修改资料后重新提交'
})

const statusClass = computed(() => {
  const s = applyData.value && applyData.value.id ? applyData.value.status : '0'
  return 'status-' + s
})

const canApply = computed(() => {
  if (!applyData.value || !applyData.value.id) return true
  return applyData.value.status === '2' // 已驳回(2) 可重新提交
})

const cardUrl = computed(() => applyData.value && applyData.value.cardImage
    ? BASE_URL + '/api/staticfiles/verify/' + applyData.value.cardImage : '')

const getMyApply = () => {
  myApplyApi({}).then(res => {
    applyData.value = res.data
  }).catch(err => {
    console.log(err)
  })
}

const onFileChange = (e) => {
  cardFile.value = e.target.files[0]
}

const handleSubmit = () => {
  if (!form.realName) {
    message.warn('请填写真实姓名')
    return
  }
  if (!form.studentNo) {
    message.warn('请填写学号')
    return
  }
  if (!form.mobile) {
    message.warn('请填写联系电话')
    return
  }
  const formData = new FormData()
  formData.append('realName', form.realName)
  formData.append('studentNo', form.studentNo)
  formData.append('college', form.college || '')
  formData.append('mobile', form.mobile)
  formData.append('serviceArea', form.serviceArea || '')
  formData.append('serviceTime', form.serviceTime || '')
  if (cardFile.value) {
    formData.append('cardFile', cardFile.value)
  }
  submitting.value = true
  applyApi(formData).then(res => {
    submitting.value = false
    message.success('认证申请已提交，请等待审核')
    getMyApply()
  }).catch(err => {
    submitting.value = false
    message.error(err.msg || '提交失败')
  })
}
</script>

<style scoped lang="less">
.content-list {
  background: #fff;
  padding: 16px 20px 40px;
}

.list-title {
  font-size: 20px;
  font-weight: 600;
  color: #152844;
  margin-bottom: 16px;
}

.status-card {
  border-radius: 8px;
  padding: 18px 20px;
  margin-bottom: 20px;

  .status-title {
    font-size: 17px;
    font-weight: 600;
    margin-bottom: 6px;
  }

  .status-desc {
    font-size: 13px;
    opacity: .85;
  }
}

.status-0 {
  background: #f4f7fb;
  color: #45608c;
}

.status-1 {
  background: #fff8e6;
  color: #ad7b00;
}

.status-2 {
  background: #eaf8ee;
  color: #217d3b;
}

.status-3 {
  background: #fdeeee;
  color: #b02a2a;
}

.apply-info {
  background: #fafbfc;
  border: 1px solid #eef1f5;
  border-radius: 6px;
  padding: 16px 20px;
  margin-bottom: 20px;

  .info-row {
    font-size: 14px;
    color: #333;
    line-height: 30px;

    .label {
      display: inline-block;
      width: 80px;
      color: #999;
    }
  }

  .reject-text {
    color: #b02a2a;
  }

  .card-img {
    width: 160px;
    height: 110px;
    object-fit: cover;
    border-radius: 4px;
    vertical-align: middle;
  }
}

.verify-form {
  .form-row {
    display: flex;
    align-items: center;
    margin-bottom: 14px;

    label {
      flex: 0 0 110px;
      font-size: 14px;
      color: #333;

      .req {
        color: #f5222d;
        font-style: normal;
      }
    }

    input {
      flex: 1;
      height: 36px;
      border: 1px solid #cedce4;
      border-radius: 4px;
      padding: 0 10px;
      font-size: 14px;
      outline: none;
    }
  }

  .submit-row {
    margin-top: 24px;
    text-align: center;
  }

  .submit-btn {
    background: #4684e2;
    color: #fff;
    border: none;
    border-radius: 4px;
    height: 40px;
    line-height: 40px;
    padding: 0 40px;
    font-size: 15px;
    cursor: pointer;

    &:disabled {
      background: #a8c1e8;
    }
  }
}
</style>