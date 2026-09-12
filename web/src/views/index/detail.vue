<template>
  <div class="detail">
    <Header/>
    <div class="detail-content">
      <div class="task-infos-view">
        <div class="task-infos">
          <div class="task-img-box">
            <img :src="detailData.cover" alt="">
          </div>
          <div class="task-info-box">
            <div class="task-state">
              <span class="state">{{ statusText(detailData.status) }}</span>
              <span>浏览量 {{ detailData.pv || 0 }}</span>
            </div>
            <h2 class="task-name">{{ detailData.title }}</h2>
            <div class="translators reward-row">
              <span class="title">悬赏金额：</span>
              <span class="price">¥ {{ detailData.reward }}</span>
            </div>
            <div class="translators">
              <span class="title">任务类型：</span><span class="name">{{ detailData.classificationTitle }}</span>
            </div>
            <div class="translators">
              <span class="title">取件地址：</span><span class="name">{{ detailData.pickupAddress }}</span>
            </div>
            <div class="translators">
              <span class="title">送达地址：</span><span class="name">{{ detailData.deliveryAddress }}</span>
            </div>
            <div class="translators">
              <span class="title">期望送达：</span><span class="name">{{ detailData.expectTime }}</span>
            </div>
            <div class="translators">
              <span class="title">联系方式：</span><span class="name">{{ detailData.contactPhone }}</span>
            </div>
            <div class="translators">
              <span class="title">发布者：</span>
              <span class="name">{{ detailData.publisherName }}（信用分 {{ detailData.publisherCredit }}）</span>
            </div>
            <div class="translators" v-if="detailData.runnerName">
              <span class="title">接单骑手：</span><span class="name">{{ detailData.runnerName }}</span>
            </div>
            <div class="translators" v-if="detailData.remark">
              <span class="title">任务备注：</span><span class="name">{{ detailData.remark }}</span>
            </div>

            <div class="btn-row">
              <button class="buy-btn" v-if="detailData.status === '0'" @click="handleAccept">立即接单</button>
              <button class="buy-btn" v-if="isMyTask && detailData.status === '2'" @click="changeStatus('3')">确认已送达</button>
              <button class="buy-btn" v-if="isMyTask && detailData.status === '3'" @click="changeStatus('4')">确认完成</button>
              <button class="buy-btn" v-if="isRunner && detailData.status === '1'" @click="changeStatus('2')">开始配送</button>
              <button class="buy-btn" v-if="isRunner && detailData.status === '2'" @click="changeStatus('3')">标记已送达</button>
              <button class="buy-btn ghost" v-if="isMyTask && (detailData.status === '0' || detailData.status === '1')" @click="changeStatus('5')">取消任务</button>
              <button class="buy-btn ghost" @click="collect">收藏任务</button>
            </div>
          </div>
        </div>
      </div>

      <div class="tab-view">
        <div class="tab-head">
          <span class="tab" :class="{'tab-select': selectTabIndex === index}" v-for="(item, index) in tabData" :key="index"
                @click="selectTab(index)">{{ item }}</span>
          <span :style="{left: tabUnderLeft + 'px'}" class="tab-underline"></span>
        </div>

        <!-- 任务说明 -->
        <div class="tab-body" v-if="selectTabIndex === 0">
          <p class="desc">{{ detailData.description }}</p>
          <div class="meta-list">
            <div>物品规格：{{ detailData.weight || '未填写' }}</div>
            <div>接单人数：{{ detailData.orderCount || 0 }}</div>
            <div>收藏数：{{ detailData.collectCount || 0 }}</div>
            <div v-if="detailData.finishTime">完成时间：{{ getFormatTime(detailData.finishTime, true) }}</div>
          </div>
        </div>

        <!-- 评价 -->
        <div class="tab-body" v-else>
          <div class="comment-input" v-if="userStore.user_id">
            <textarea ref="commentRef" placeholder="说说这次跑腿体验吧（发布者可评价骑手）"></textarea>
            <button class="send-btn" @click="sendComment">发表评价</button>
          </div>
          <div class="comment-sort">
            <span :class="{'sort-select': sortIndex === 0}" @click="sortCommentList('recent')">最新</span>
            <span :class="{'sort-select': sortIndex === 1}" @click="sortCommentList('hot')">最热</span>
          </div>
          <div class="comment-list">
            <div class="comment-item" v-for="item in commentData" :key="item.id">
              <div class="comment-head">
                <span class="comment-user">{{ item.username }}</span>
                <span class="comment-score">{{ item.score }} 星</span>
                <span class="comment-time">{{ item.commentTime }}</span>
              </div>
              <div class="comment-content">{{ item.content }}</div>
              <div class="comment-reply" v-if="item.reply">回复：{{ item.reply }}</div>
              <div class="comment-like" @click="like(item.id)">赞 {{ item.likeCount }}</div>
            </div>
            <div class="no-data" v-if="commentData.length === 0">暂无评价</div>
          </div>
        </div>
      </div>
    </div>
    <Footer/>
  </div>
</template>
<script setup>
import {message} from "ant-design-vue";
import Header from '/@/views/index/components/header.vue'
import Footer from '/@/views/index/components/footer.vue'
import {detailApi, acceptApi, updateStatusApi, listApi as listTaskList} from '/@/api/task'
import {listTaskCommentsApi, createApi as createCommentApi, likeApi} from '/@/api/comment'
import {collectApi} from '/@/api/taskCollect'
import {BASE_URL} from "/@/store/constants";
import {useRoute, useRouter} from "vue-router/dist/vue-router";
import {useUserStore} from "/@/store";
import {getFormatTime} from "/@/utils";

const router = useRouter()
const route = useRoute()
const userStore = useUserStore();

const STATUS_TEXT = {
  '0': '待接单',
  '1': '已接单',
  '2': '配送中',
  '3': '已送达',
  '4': '已完成',
  '5': '已取消',
  '6': '纠纷中'
}

let taskId = ref('')
let detailData = ref({})
let tabUnderLeft = ref(6)
let tabData = ref(['任务说明', '评价'])
let selectTabIndex = ref(0)

let commentData = ref([])
let recommendData = ref([])
let sortIndex = ref(0)
let order = ref('recent')

let commentRef = ref()

const isMyTask = computed(() => String(detailData.value.publisherId) === String(userStore.user_id))
const isRunner = computed(() => String(detailData.value.runnerId) === String(userStore.user_id))

onMounted(() => {
  taskId.value = route.query.id.trim()
  getTaskDetail()
  getCommentList()
})

const statusText = (status) => STATUS_TEXT[status] || '未知'

const selectTab = (index) => {
  selectTabIndex.value = index
  tabUnderLeft.value = 6 + 90 * index
}

const getTaskDetail = () => {
  detailApi({id: taskId.value}).then(res => {
    detailData.value = res.data
    if (detailData.value.cover) {
      detailData.value.cover = BASE_URL + '/api/staticfiles/image/' + detailData.value.cover
    }
  }).catch(err => {
    message.error('获取任务详情失败')
  })
}

// 接单
const handleAccept = () => {
  if (!userStore.user_id) {
    message.warn('请先登录')
    router.push({name: 'login'})
    return
  }
  acceptApi({taskId: taskId.value}).then(res => {
    message.success(res.msg || '接单成功')
    getTaskDetail()
  }).catch(err => {
    message.error(err.msg || '接单失败')
  })
}

// 任务状态流转
const changeStatus = (status) => {
  updateStatusApi({taskId: taskId.value, status: status}).then(res => {
    message.success('操作成功')
    getTaskDetail()
  }).catch(err => {
    message.error('操作失败')
  })
}

const collect = () => {
  let userId = userStore.user_id
  if (userId) {
    collectApi({taskId: taskId.value, userId: userId}).then(res => {
      message.success(res.msg)
      getTaskDetail()
    }).catch(err => {
      message.error('收藏失败')
    })
  } else {
    message.warn('请先登录')
  }
}

const sendComment = () => {
  let text = commentRef.value.value.trim()
  if (text.length <= 0) {
    return
  }
  commentRef.value.value = ''
  let userId = userStore.user_id
  if (!userId) {
    message.warn('请先登录！')
    router.push({name: 'login'})
    return
  }
  createCommentApi({
    content: text,
    taskId: taskId.value,
    fromUserId: userId,
    toUserId: detailData.value.runnerId,
    role: '1',
    score: '5'
  }).then(res => {
    getCommentList()
  }).catch(err => {
    console.log(err)
  })
}

const like = (commentId) => {
  likeApi({id: commentId}).then(res => {
    getCommentList()
  }).catch(err => {
    console.log(err)
  })
}

const getCommentList = () => {
  listTaskCommentsApi({taskId: taskId.value, order: order.value}).then(res => {
    res.data.forEach(item => {
      item.commentTime = getFormatTime(item.commentTime, true)
    })
    commentData.value = res.data
  }).catch(err => {
    console.log(err)
  })
}

const sortCommentList = (sortType) => {
  sortIndex.value = sortType === 'recent' ? 0 : 1
  order.value = sortType
  getCommentList()
}
</script>
<style scoped lang="less">

.detail-content {
  display: flex;
  flex-direction: column;
  width: 1100px;
  margin: 4px auto;
}

.task-infos-view {
  display: flex;
  margin: 40px 0 20px;
  overflow: hidden;

  .task-infos {
    flex: 1;
    display: flex;
  }

  .task-img-box {
    flex: 0 0 255px;
    margin: 0 40px 0 0;

    img {
      width: 255px;
      height: 200px;
      display: block;
      background-size: cover;
      object-fit: cover;
    }
  }

  .task-info-box {
    flex: 1;
    text-align: left;
  }

  .task-state {
    height: 26px;
    line-height: 26px;

    .state {
      font-weight: 500;
      color: #4684e2;
      background: rgba(70, 132, 226, .1);
      border-radius: 2px;
      padding: 5px 8px;
      margin-right: 16px;
    }

    span {
      font-size: 14px;
      color: #152844;
    }
  }

  .task-name {
    line-height: 32px;
    margin: 16px 0;
    color: #0F1111;
    font-size: 18px;
    font-weight: 400;
  }

  .translators {
    line-height: 20px;
    font-size: 14px;
    margin: 6px 0;

    .title {
      color: #787878;
    }

    .name {
      color: #315c9e;
    }
  }

  .reward-row {
    margin: 10px 0;

    .price {
      color: #ff7b31;
      font-size: 22px;
      font-weight: 600;
    }
  }
}

.btn-row {
  margin-top: 18px;
  display: flex;
  flex-wrap: wrap;
}

.buy-btn {
  cursor: pointer;
  background: #4684e2;
  border-radius: 4px;
  text-align: center;
  color: #fff;
  font-size: 14px;
  height: 36px;
  line-height: 36px;
  padding: 0 20px;
  outline: none;
  border: none;
  margin: 0 12px 12px 0;
}

.buy-btn.ghost {
  background: #fff;
  color: #4684e2;
  border: 1px solid #4684e2;
}

.tab-view {
  margin-top: 20px;
}

.tab-head {
  position: relative;
  height: 40px;
  line-height: 40px;
  border-bottom: 1px solid #cedce4;

  .tab {
    font-size: 16px;
    color: #5f77a6;
    margin-right: 40px;
    cursor: pointer;
  }

  .tab-select {
    color: #152844;
    font-weight: 600;
  }

  .tab-underline {
    position: absolute;
    bottom: -1px;
    width: 32px;
    height: 3px;
    background: #4684e2;
    transition: left .3s;
  }
}

.tab-body {
  padding: 20px 0;
  font-size: 14px;
  color: #333;
  line-height: 24px;

  .desc {
    margin-bottom: 16px;
  }

  .meta-list {
    div {
      color: #666;
      line-height: 26px;
    }
  }
}

.comment-input {
  display: flex;
  flex-direction: column;
  margin-bottom: 20px;

  textarea {
    width: 100%;
    height: 72px;
    border: 1px solid #cedce4;
    border-radius: 4px;
    padding: 8px;
    outline: none;
    resize: none;
    font-size: 14px;
  }

  .send-btn {
    margin-top: 10px;
    align-self: flex-end;
    background: #4684e2;
    color: #fff;
    border: none;
    border-radius: 4px;
    height: 32px;
    line-height: 32px;
    padding: 0 20px;
    cursor: pointer;
  }
}

.comment-sort {
  margin-bottom: 12px;

  span {
    margin-right: 16px;
    color: #999;
    cursor: pointer;
    font-size: 14px;
  }

  .sort-select {
    color: #4684e2;
  }
}

.comment-item {
  padding: 14px 0;
  border-bottom: 1px solid #f0f0f0;

  .comment-head {
    font-size: 13px;
    color: #999;
    margin-bottom: 6px;

    .comment-user {
      color: #315c9e;
      margin-right: 12px;
    }

    .comment-score {
      color: #ff7b31;
      margin-right: 12px;
    }
  }

  .comment-content {
    font-size: 14px;
    color: #333;
  }

  .comment-reply {
    margin-top: 6px;
    background: #f6f9fb;
    padding: 8px;
    font-size: 13px;
    color: #666;
    border-radius: 4px;
  }

  .comment-like {
    margin-top: 6px;
    font-size: 13px;
    color: #999;
    cursor: pointer;
  }
}

.no-data {
  padding: 40px 0;
  text-align: center;
  color: #999;
}
</style>