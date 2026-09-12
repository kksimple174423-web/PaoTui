<template>
  <div class="content-list">
    <div class="list-title">我的任务</div>

    <a-tabs default-active-key="publish" @change="onTabChange">
      <a-tab-pane key="publish" tab="我发布的" />
      <a-tab-pane key="accept" tab="我接的单" />
    </a-tabs>

    <div class="filter-bar">
      <span v-for="item in statusFilters" :key="item.value"
            class="filter-item" :class="{'filter-select': status === item.value}"
            @click="filterStatus(item.value)">{{ item.label }}</span>
    </div>

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
              <div class="row"><span class="label">路线</span>{{ item.pickupAddress }} → {{ item.deliveryAddress }}</div>
              <div class="row" v-if="tab === 'publish'">
                <span class="label">骑手</span>{{ item.runnerName || '等待接单' }}
              </div>
              <div class="row" v-else>
                <span class="label">发布者</span>{{ item.publisherName }}
              </div>
              <div class="row"><span class="label">类型</span>{{ item.classificationTitle }}</div>
              <div class="row"><span class="label">发布</span>{{ getFormatTime(item.createTime, true) }}</div>
            </div>
          </div>
          <div class="task-actions">
            <a-button size="small" @click="handleDetail(item.id)">查看详情</a-button>

            <template v-if="tab === 'publish'">
              <a-popconfirm v-if="item.status === '0' || item.status === '1'" title="确定取消这个任务？"
                            ok-text="是" cancel-text="否" @confirm="changeStatus(item, '5')">
                <a-button size="small">取消任务</a-button>
              </a-popconfirm>
              <a-button size="small" type="primary" v-if="item.status === '2'" @click="changeStatus(item, '3')">确认已送达</a-button>
              <a-button size="small" type="primary" v-if="item.status === '3'" @click="changeStatus(item, '4')">确认完成</a-button>
              <a-button size="small" type="primary" v-if="item.status === '4'" @click="openComment(item)">评价骑手</a-button>
            </template>

            <template v-else>
              <a-button size="small" type="primary" v-if="item.status === '1'" @click="changeStatus(item, '2')">开始配送</a-button>
              <a-button size="small" type="primary" v-if="item.status === '2'" @click="changeStatus(item, '3')">标记已送达</a-button>
              <a-button size="small" type="primary" v-if="item.status === '4'" @click="openComment(item)">评价发布者</a-button>
            </template>
          </div>
        </div>
        <div class="no-data" v-if="taskData.length === 0 && !loading">暂无任务</div>
      </div>
    </a-spin>

    <a-modal v-model:visible="commentVisible" title="发表评价" :confirm-loading="submitting" @ok="submitComment">
      <div class="comment-form">
        <div class="score-row">
          <span>评分</span>
          <span v-for="n in 5" :key="n" class="star" :class="{'star-on': n <= commentScore}" @click="commentScore = n">★</span>
        </div>
        <textarea v-model="commentContent" rows="4" placeholder="说说这次跑腿的体验，评价会影响对方信用分"></textarea>
      </div>
    </a-modal>
  </div>
</template>

<script setup>
import {message} from "ant-design-vue";
import {getFormatTime} from '/@/utils/'
import {myPublishApi, myAcceptApi, updateStatusApi} from '/@/api/task'
import {createApi as createCommentApi} from '/@/api/comment'
import {BASE_URL} from "/@/store/constants";
import {useUserStore} from "/@/store";

const router = useRouter();
const userStore = useUserStore();

const STATUS_TEXT = {
  '0': '待接单', '1': '已接单', '2': '配送中', '3': '已送达待确认', '4': '已完成', '5': '已取消', '6': '纠纷中'
}

const statusFilters = [
  {label: '全部', value: ''},
  {label: '待接单', value: '0'},
  {label: '已接单', value: '1'},
  {label: '配送中', value: '2'},
  {label: '待确认', value: '3'},
  {label: '已完成', value: '4'},
  {label: '已取消', value: '5'}
]

const tab = ref('publish')
const status = ref('')
const loading = ref(false)
const taskData = ref([])

const commentVisible = ref(false)
const commentScore = ref(5)
const commentContent = ref('')
const currentTask = ref({})
const submitting = ref(false)

onMounted(() => {
  getTaskList()
})

const statusText = (s) => STATUS_TEXT[s] || '未知'

const onTabChange = (key) => {
  tab.value = key
  status.value = ''
  getTaskList()
}

const filterStatus = (s) => {
  status.value = s
  getTaskList()
}

const getTaskList = () => {
  loading.value = true
  const api = tab.value === 'publish' ? myPublishApi : myAcceptApi
  api({status: status.value}).then(res => {
    res.data.forEach(item => {
      if (item.cover) {
        item.cover = BASE_URL + '/api/staticfiles/image/' + item.cover
      }
    })
    taskData.value = res.data
    loading.value = false
  }).catch(err => {
    console.log(err)
    loading.value = false
  })
}

const changeStatus = (item, s) => {
  updateStatusApi({taskId: item.id, status: s}).then(res => {
    message.success('操作成功')
    getTaskList()
  }).catch(err => {
    message.error('操作失败')
  })
}

const openComment = (item) => {
  currentTask.value = item
  commentScore.value = 5
  commentContent.value = ''
  commentVisible.value = true
}

const submitComment = () => {
  if (!commentContent.value.trim()) {
    message.warn('请填写评价内容')
    return
  }
  submitting.value = true
  createCommentApi({
    taskId: currentTask.value.id,
    fromUserId: userStore.user_id,
    toUserId: tab.value === 'publish' ? currentTask.value.runnerId : currentTask.value.publisherId,
    role: tab.value === 'publish' ? '1' : '2',
    score: String(commentScore.value),
    content: commentContent.value
  }).then(res => {
    submitting.value = false
    commentVisible.value = false
    message.success('评价成功，对方信用分已更新')
  }).catch(err => {
    submitting.value = false
    message.error('评价失败')
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
  margin-bottom: 12px;
}

.filter-bar {
  margin: 8px 0 16px;

  .filter-item {
    display: inline-block;
    font-size: 13px;
    color: #666;
    margin-right: 18px;
    cursor: pointer;
    padding: 2px 0;
  }

  .filter-select {
    color: #4684e2;
    border-bottom: 2px solid #4684e2;
  }
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

.comment-form {
  .score-row {
    margin-bottom: 12px;
    font-size: 14px;
    color: #333;

    span:first-child {
      margin-right: 10px;
    }
  }

  .star {
    font-size: 22px;
    color: #ddd;
    cursor: pointer;
    margin-right: 4px;
  }

  .star-on {
    color: #ff9a2e;
  }

  textarea {
    width: 100%;
    border: 1px solid #cedce4;
    border-radius: 4px;
    padding: 8px;
    font-size: 14px;
    outline: none;
    resize: none;
  }
}
</style>