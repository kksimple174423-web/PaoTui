<template>
  <div>
    <div class="page-view">
      <div class="table-operations">
        <a-space>
          <span style="margin-right: 8px;">处理状态：</span>
          <a-radio-group v-model:value="data.status" @change="getDataList">
            <a-radio-button value="">全部</a-radio-button>
            <a-radio-button value="0">待处理</a-radio-button>
            <a-radio-button value="1">已打款</a-radio-button>
            <a-radio-button value="2">已驳回</a-radio-button>
          </a-radio-group>
        </a-space>
      </div>
      <a-table
          size="middle"
          rowKey="id"
          :loading="data.loading"
          :columns="columns"
          :data-source="data.dataList"
          :scroll="{ x: 'max-content' }"
          :pagination="{ size: 'default', pageSize: 10, showSizeChanger: false }"
      >
        <template #bodyCell="{ text, record, column }">
          <template v-if="column.key === 'operation'">
            <span v-if="record.status === '0'">
              <a-popconfirm title="确认已打款？" ok-text="是" cancel-text="否" @confirm="handleAudit(record, '1')">
                <a>确认打款</a>
              </a-popconfirm>
              <a-divider type="vertical" />
              <a @click="openReject(record)">驳回</a>
            </span>
            <span v-else style="color: #999;">已处理</span>
          </template>
          <template v-else-if="column.key === 'status'">
            <a-tag :color="statusColor(text)">{{ statusText(text) }}</a-tag>
          </template>
          <template v-else-if="column.key === 'amount'">
            <span style="color: #f5222d;font-weight: 600;">¥ {{ text }}</span>
          </template>
        </template>
      </a-table>
    </div>

    <a-modal v-model:visible="rejectVisible" title="驳回提现申请" @ok="submitReject">
      <a-textarea v-model:value="rejectRemark" :rows="3" placeholder="请填写驳回原因，例如：收款账号有误"></a-textarea>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { message } from 'ant-design-vue';
import { allWithdrawApi, auditWithdrawApi } from '/@/api/wallet';
import {getFormatTime} from '/@/utils/'

const STATUS_TEXT: Record<string, string> = {'0': '待处理', '1': '已打款', '2': '已驳回'}
const statusText = (s: string) => STATUS_TEXT[s] || '未知'
const statusColor = (s: string) => s === '0' ? 'blue' : (s === '1' ? 'green' : 'red')

const columns = reactive([
  { title: '序号', dataIndex: 'index', key: 'index', width: 60 },
  { title: '申请人', dataIndex: 'username', key: 'username',
    customRender: ({ record }: any) => record.nickname ? `${record.nickname}(${record.username})` : record.username },
  { title: '提现金额', dataIndex: 'amount', key: 'amount', width: 110 },
  { title: '收款方式', dataIndex: 'accountType', key: 'accountType' },
  { title: '收款账号', dataIndex: 'account', key: 'account' },
  { title: '收款姓名', dataIndex: 'realName', key: 'realName',
    customRender: ({ text }: any) => text || '--' },
  { title: '状态', dataIndex: 'status', key: 'status', width: 100 },
  { title: '处理意见', dataIndex: 'handleRemark', key: 'handleRemark',
    customRender: ({ text }: any) => text || '--' },
  { title: '申请时间', dataIndex: 'applyTime', key: 'applyTime',
    customRender: ({ text }: any) => getFormatTime(text, true) },
  { title: '操作', dataIndex: 'action', key: 'operation', align: 'center' as const, fixed: 'right' as const, width: 150 },
]);

const data = reactive({
  dataList: [] as any[],
  loading: false,
  status: '',
});

const rejectVisible = ref(false);
const rejectRemark = ref('');
const currentRecord = ref<any>(null);

onMounted(() => {
  getDataList();
});

const getDataList = () => {
  data.loading = true;
  allWithdrawApi({ status: data.status })
      .then((res: any) => {
        data.loading = false;
        res.data.forEach((item: any, index: number) => {
          item.index = index + 1;
        });
        data.dataList = res.data;
      })
      .catch((err: any) => {
        data.loading = false;
        console.log(err);
      });
}

const handleAudit = (record: any, status: string) => {
  auditWithdrawApi({ id: record.id, status: status, handleRemark: status === '1' ? '已打款' : '' })
      .then((res: any) => {
        message.success('已处理');
        getDataList();
      })
      .catch((err: any) => {
        message.error(err.msg || '操作失败');
      });
}

const openReject = (record: any) => {
  currentRecord.value = record;
  rejectRemark.value = '';
  rejectVisible.value = true;
}

const submitReject = () => {
  if (!rejectRemark.value.trim()) {
    message.warn('请填写驳回原因');
    return;
  }
  auditWithdrawApi({ id: currentRecord.value.id, status: '2', handleRemark: rejectRemark.value })
      .then((res: any) => {
        message.success('已驳回');
        rejectVisible.value = false;
        getDataList();
      })
      .catch((err: any) => {
        message.error(err.msg || '操作失败');
      });
}
</script>

<style scoped lang="less">
.page-view {
  min-height: 100%;
  background: #fff;
  padding: 24px;
  display: flex;
  flex-direction: column;
}

.table-operations {
  margin-bottom: 16px;
  text-align: right;
}
</style>