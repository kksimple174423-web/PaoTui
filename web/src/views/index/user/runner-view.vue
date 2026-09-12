<template>
  <div class="content-list">
    <div class="list-title">骑手工作台</div>

    <div class="runner-card">
      <div class="stat">
        <div class="num">{{ profile.finishCount || 0 }}</div>
        <div class="label">完成单数</div>
      </div>
      <div class="stat">
        <div class="num">¥ {{ profile.totalIncome || '0.00' }}</div>
        <div class="label">累计收益</div>
      </div>
      <div class="stat">
        <div class="num">{{ profile.creditScore || 100 }}</div>
        <div class="label">信用分</div>
      </div>
      <div class="stat">
        <div class="num small">{{ runnerStatusText }}</div>
        <div class="label">认证状态</div>
      </div>
    </div>

    <div class="cert-tip" v-if="profile.runnerStatus !== '2'">
      你还没有通过骑手认证，认证通过后即可接单（当前为：{{ runnerStatusText }}）。
    </div>

    <a-tabs default-active-key="hall" @change="onTabChange">
      <a-tab-pane key="hall" tab="可接任务" />
      <a-tab-pane key="doing" tab="进行中" />
      <a-tab-pane key="done" tab="已完成" />
    </a-tabs>

    <a-spin :spinning="loading">
      <div class="list-content">
        <div class="task-item" v-for="item in taskData" :key="item.id">
          <div class="task-head">
            <span class="task-title" @click="handleDetail(item.id)">{{ item.title }}</span>
            <span class="state">{{ statusText(item.status) }}</span>
          </div>
          <div class="task-body">
            <img v-if="item.cover" :src="item.cover" class="task-cover">
            <div class="task-info">
              <div class="row"><span class="label">悬赏</span><span class="reward">¥ {{ item.reward }}</span></div>
              <div class="row"><span class="label">取件</span>{{ item.pickupAddress }}</div>
              <div class="row"><span class="label">送达</span>{{ item.deliveryAddress }}</div>
              <div class="row"><span class="label">期望</span>{{ item.expectTime }}</div>
              <div class="row"><span class="label">发布者</span>{{ item.publisherName }}</div>
            </div>
          </div>
          <div class="task-actions">
            <a-button size="small" @click="handleDetail(item.id)">查看详情</a-button>
            <a-button size="small" type="primary" v-if="tab === 'hall'" @click="handleAccept(item)">立即接单</a-button>
            <a-button size="small" type="primary" v-if="item.status === '1'" @click="changeStatus(item, '2')">开始配送</a-button>
            <a-button size="small" type="primary" v-if="item.status === '2'" @click="changeStatus(item, '3')">标记已送达</a-button>
          </div>
        </div>
        <div class="no-data" v-if="taskData.length === 0 && !loading">暂无任务</div>
      </div>
    </a-spin>
  </div>
</template>

<script setup>
import {message} from "ant-design-vue";
import {detailApi as userDetailApi} from '/@/api/user'
import {hallApi, myAcceptApi, acceptApi, updateStatusApi} from '/@/api/task'
import {BASE_URL} from "/@/store/constants";
import {useUserStore} from "/@/store";

const router = useRouter();
const userStore = useUserStore();

const STATUS_TEXT = {
  '0': '待接单', '1': '已接单', '2': '配送中', '3': '已送达待确认', '4': '已完成', '5': '已取消', '6': '纠纷中'
}
const RUNNER_TEXT = {'0': '未申请', '1': '审核中', '2': '已认证', '3': '已驳回'}

const tab = ref('hall')
const loading = ref(false)
const taskData = ref([])
const profile = ref({})

onMounted(() => {
  getProfile()
  getTaskList()
})

const statusText = (s) => STATUS_TEXT[s] || '未知'
const runnerStatusText = computed(() => RUNNER_TEXT[profile.value.runnerStatus] || '未申请')

const getProfile = () => {
  userDetailApi({userId: userStore.user_id}).then(res => {
    profile.value = res.data || {}
  }).catch(err => {
    console.log(err)
  })
}

const onTabChange = (key) => {
  tab.value = key
  getTaskList()
}

const getTaskList = () => {
  loading.value = true
  if (tab.value === 'hall') {
    hallApi({}).then(res => {
      taskData.value = formatList(res.data)
      loading.value = false
    }).catch(err => {
      console.log(err)
      loading.value = false
    })
    return
  }
  myAcceptApi({}).then(res => {
    let list = res.data || []
    if (tab.value === 'doing') {
      list = list.filter(item => ['1', '2', '3'].indexOf(item.status) >= 0)
    } else {
      list = list.filter(item => item.status === '4')
    }
    taskData.value = formatList(list)
    loading.value = false
  }).catch(err => {
    console.log(err)
    loading.value = false
  })
}

const formatList = (list) => {
  (list || []).forEach(item => {
    if (item.cover) {
      item.cover = BASE_URL + '/api/staticfiles/image/' + item.cover
    }
  })
  return list || []
}

const handleAccept = (item) => {
  acceptApi({taskId: item.id}).then(res => {
    message.success('接单成功，请尽快取件')
    tab.value = 'doing'
    getTaskList()
  }).catch(err => {
    message.error(err.msg || '接单失败')
  })
}

const changeStatus = (item, s) => {
  updateStatusApi({taskId: item.id, status: s}).then(res => {
    message.success('操作成功')
    getTaskList()
    getProfile()
  }).catch(err => {
    message.error('操作失败')
  })
}

const handleDetail = (id) => {
  let text = router.resolve({name: 'detail', query: {id: id}})
  window.open(text.href, '_blank')
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

.runner-card {
  display: flex;
  background: linear-gradient(90deg, #4684e2, #6aa3f0);
  border-radius: 8px;
  padding: 18px 0;
  margin-bottom: 16px;

  .stat {
    flex: 1;
    text-align: center;
    color: #fff;

    .num {
      font-size: 22px;
      font-weight: 600;
      line-height: 30px;
    }

    .num.small {
      font-size: 16px;
    }

    .label {
      font-size: 12px;
      opacity: .85;
    }
  }
}

.cert-tip {
  background: #fff8e6;
  border: 1px solid #ffe1a8;
  color: #ad7b00;
  font-size: 13px;
  padding: 10px 12px;
  border-radius: 4px;
  margin-bottom: 16px;
}

.task-item {
  border: 1px solid #eef1f5;
  border-radius: 6px;
  padding: 14px 16px;
  margin-bottom: 14px;

  .task-head {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .task-title {
    font-size: 16px;
    color: #152844;
    font-weight: 500;
    cursor: pointer;

    &:hover {
      color: #4684e2;
    }
  }

  .state {
    font-size: 12px;
    color: #4684e2;
    background: rgba(70, 132, 226, .1);
    border-radius: 2px;
    padding: 3px 8px;
  }

  .task-body {
    display: flex;
    margin-top: 12px;

    .task-cover {
      width: 96px;
      height: 72px;
      object-fit: cover;
      border-radius: 4px;
      margin-right: 16px;
    }

    .task-info {
      flex: 1;
      font-size: 13px;
      color: #333;

      .row {
        line-height: 24px;
      }

      .label {
        display: inline-block;
        width: 56px;
        color: #999;
      }

      .reward {
        color: #ff7b31;
        font-weight: 600;
      }
    }
  }

  .task-actions {
    margin-top: 12px;
    text-align: right;

    button {
      margin-left: 8px;
    }
  }
}

.no-data {
  padding: 60px 0;
  text-align: center;
  color: #999;
}
</style>