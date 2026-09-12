<template>
  <div class="content-list">
    <div class="list-title">我的钱包</div>

    <div class="wallet-card">
      <div class="balance-box">
        <div class="label">可用余额（元）</div>
        <div class="balance">{{ wallet.balance || '0.00' }}</div>
      </div>
      <div class="sub-stats">
        <div class="stat">
          <div class="num">{{ wallet.frozen || '0.00' }}</div>
          <div class="label">冻结中（赏金托管）</div>
        </div>
        <div class="stat">
          <div class="num">{{ wallet.totalIncome || '0.00' }}</div>
          <div class="label">累计收入</div>
        </div>
        <div class="stat">
          <div class="num">{{ wallet.totalExpense || '0.00' }}</div>
          <div class="label">累计支出</div>
        </div>
      </div>
      <div class="btn-row">
        <button class="w-btn" @click="openRecharge">充值</button>
        <button class="w-btn ghost" @click="openWithdraw">提现</button>
      </div>
    </div>

    <a-tabs default-active-key="records" @change="onTabChange">
      <a-tab-pane key="records" tab="资金明细" />
      <a-tab-pane key="withdraws" tab="提现记录" />
    </a-tabs>

    <a-spin :spinning="loading">
      <!-- 资金明细 -->
      <div v-if="tab === 'records'">
        <div class="record-item" v-for="item in records" :key="item.id">
          <div class="left">
            <div class="type">{{ item.typeText }}</div>
            <div class="time">{{ getFormatTime(item.createTime, true) }}</div>
            <div class="remark">{{ item.remark }}</div>
          </div>
          <div class="right">
            <div class="amount" :class="{'income': Number(item.amount) > 0}">
              {{ Number(item.amount) > 0 ? '+' : '' }}{{ item.amount }}
            </div>
            <div class="after">余额 {{ item.balanceAfter }}</div>
          </div>
        </div>
        <div class="no-data" v-if="records.length === 0 && !loading">暂无资金记录</div>
      </div>

      <!-- 提现记录 -->
      <div v-else>
        <div class="record-item" v-for="item in withdraws" :key="item.id">
          <div class="left">
            <div class="type">提现 {{ item.amount }} 元</div>
            <div class="time">{{ getFormatTime(item.applyTime, true) }}</div>
            <div class="remark">{{ item.accountType }} {{ item.account }} {{ item.realName }}</div>
            <div class="remark" v-if="item.handleRemark">处理意见：{{ item.handleRemark }}</div>
          </div>
          <div class="right">
            <a-tag :color="withdrawColor(item.status)">{{ withdrawText(item.status) }}</a-tag>
          </div>
        </div>
        <div class="no-data" v-if="withdraws.length === 0 && !loading">暂无提现记录</div>
      </div>
    </a-spin>

    <!-- 充值弹窗 -->
    <a-modal v-model:visible="rechargeVisible" title="钱包充值" :confirm-loading="submitting" @ok="submitRecharge">
      <div class="modal-row">
        <span>充值金额</span>
        <a-input-number v-model:value="rechargeAmount" :min="1" :max="10000" style="width: 200px;"/>
      </div>
      <p class="tip">演示环境：充值金额将直接进入账户余额</p>
    </a-modal>

    <!-- 提现弹窗 -->
    <a-modal v-model:visible="withdrawVisible" title="申请提现" :confirm-loading="submitting" @ok="submitWithdraw">
      <div class="modal-row">
        <span>提现金额</span>
        <a-input-number v-model:value="withdrawForm.amount" :min="1" :max="Number(wallet.balance || 0)" style="width: 200px;"/>
      </div>
      <div class="modal-row">
        <span>收款方式</span>
        <a-select v-model:value="withdrawForm.accountType" style="width: 200px;">
          <a-select-option value="支付宝">支付宝</a-select-option>
          <a-select-option value="微信">微信</a-select-option>
          <a-select-option value="银行卡">银行卡</a-select-option>
        </a-select>
      </div>
      <div class="modal-row">
        <span>收款账号</span>
        <a-input v-model:value="withdrawForm.account" style="width: 200px;" placeholder="请输入账号"/>
      </div>
      <div class="modal-row">
        <span>收款姓名</span>
        <a-input v-model:value="withdrawForm.realName" style="width: 200px;" placeholder="请输入姓名"/>
      </div>
    </a-modal>
  </div>
</template>

<script setup>
import {message} from "ant-design-vue";
import {myApi, recordsApi, rechargeApi, withdrawApi, withdrawListApi} from '/@/api/wallet'
import {getFormatTime} from '/@/utils/'

const tab = ref('records')
const loading = ref(false)
const wallet = ref({})
const records = ref([])
const withdraws = ref([])

const rechargeVisible = ref(false)
const rechargeAmount = ref(50)
const withdrawVisible = ref(false)
const withdrawForm = reactive({amount: 10, accountType: '支付宝', account: '', realName: ''})
const submitting = ref(false)

onMounted(() => {
  getWallet()
  getRecords()
})

const onTabChange = (key) => {
  tab.value = key
  if (key === 'records') {
    getRecords()
  } else {
    getWithdraws()
  }
}

const withdrawText = (s) => s === '0' ? '待处理' : (s === '1' ? '已打款' : '已驳回')
const withdrawColor = (s) => s === '0' ? 'blue' : (s === '1' ? 'green' : 'red')

const getWallet = () => {
  myApi({}).then(res => {
    wallet.value = res.data || {}
  }).catch(err => console.log(err))
}

const getRecords = () => {
  loading.value = true
  recordsApi({}).then(res => {
    records.value = res.data || []
    loading.value = false
  }).catch(err => {
    loading.value = false
  })
}

const getWithdraws = () => {
  loading.value = true
  withdrawListApi({}).then(res => {
    withdraws.value = res.data || []
    loading.value = false
  }).catch(err => {
    loading.value = false
  })
}

const openRecharge = () => {
  rechargeAmount.value = 50
  rechargeVisible.value = true
}

const submitRecharge = () => {
  if (!rechargeAmount.value || rechargeAmount.value <= 0) {
    message.warn('请输入充值金额')
    return
  }
  submitting.value = true
  rechargeApi({amount: rechargeAmount.value}).then(res => {
    submitting.value = false
    rechargeVisible.value = false
    message.success('充值成功')
    getWallet()
    if (tab.value === 'records') getRecords()
  }).catch(err => {
    submitting.value = false
    message.error(err.msg || '充值失败')
  })
}

const openWithdraw = () => {
  withdrawForm.amount = 10
  withdrawForm.accountType = '支付宝'
  withdrawForm.account = ''
  withdrawForm.realName = ''
  withdrawVisible.value = true
}

const submitWithdraw = () => {
  if (!withdrawForm.amount || withdrawForm.amount <= 0) {
    message.warn('请输入提现金额')
    return
  }
  if (!withdrawForm.account) {
    message.warn('请输入收款账号')
    return
  }
  submitting.value = true
  withdrawApi({
    amount: withdrawForm.amount,
    accountType: withdrawForm.accountType,
    account: withdrawForm.account,
    realName: withdrawForm.realName
  }).then(res => {
    submitting.value = false
    withdrawVisible.value = false
    message.success('提现申请已提交，等待管理员处理')
    getWallet()
    tab.value = 'withdraws'
    getWithdraws()
  }).catch(err => {
    submitting.value = false
    message.error(err.msg || '提现失败')
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

.wallet-card {
  background: linear-gradient(120deg, #2f6fd0, #4f93ea);
  border-radius: 10px;
  padding: 22px 24px;
  color: #fff;
  margin-bottom: 18px;

  .balance-box {
    .label {
      font-size: 13px;
      opacity: .85;
    }

    .balance {
      font-size: 34px;
      font-weight: 700;
      line-height: 44px;
    }
  }

  .sub-stats {
    display: flex;
    margin-top: 16px;

    .stat {
      flex: 1;

      .num {
        font-size: 16px;
        font-weight: 600;
      }

      .label {
        font-size: 12px;
        opacity: .8;
      }
    }
  }

  .btn-row {
    margin-top: 18px;

    .w-btn {
      background: #fff;
      color: #2f6fd0;
      border: none;
      border-radius: 4px;
      height: 32px;
      line-height: 32px;
      padding: 0 22px;
      font-size: 14px;
      cursor: pointer;
      margin-right: 12px;
    }

    .w-btn.ghost {
      background: transparent;
      color: #fff;
      border: 1px solid rgba(255, 255, 255, .8);
    }
  }
}

.record-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 4px;
  border-bottom: 1px solid #f2f4f7;

  .type {
    font-size: 15px;
    color: #152844;
    font-weight: 500;
  }

  .time, .remark {
    font-size: 12px;
    color: #999;
    line-height: 20px;
  }

  .amount {
    font-size: 17px;
    font-weight: 600;
    color: #f5222d;
    text-align: right;
  }

  .amount.income {
    color: #22a06b;
  }

  .after {
    font-size: 12px;
    color: #999;
    text-align: right;
  }
}

.no-data {
  padding: 50px 0;
  text-align: center;
  color: #999;
}

.modal-row {
  display: flex;
  align-items: center;
  margin-bottom: 14px;

  span:first-child {
    display: inline-block;
    width: 80px;
    color: #333;
    font-size: 14px;
  }
}

.tip {
  color: #999;
  font-size: 12px;
}
</style>