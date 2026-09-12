<template>
  <div>
    <div class="page-view">
      <div class="table-operations">
        <a-space>
          <span style="margin-right: 8px;">审核状态：</span>
          <a-radio-group v-model:value="data.status" @change="getDataList">
            <a-radio-button value="">全部</a-radio-button>
            <a-radio-button value="0">待审核</a-radio-button>
            <a-radio-button value="1">已通过</a-radio-button>
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
              <a-popconfirm title="确定通过该认证？" ok-text="是" cancel-text="否" @confirm="handleAudit(record, '1')">
                <a>通过</a>
              </a-popconfirm>
              <a-divider type="vertical" />
              <a @click="openReject(record)">驳回</a>
            </span>
            <span v-else style="color: #999;">已处理</span>
          </template>
          <template v-else-if="column.key === 'status'">
            <a-tag :color="statusColor(text)">{{ statusText(text) }}</a-tag>
          </template>
          <template v-else-if="column.key === 'auditRemark'">
            <span>{{ text || '--' }}</span>
          </template>
        </template>
      </a-table>
    </div>

    <a-modal v-model:visible="rejectVisible" title="驳回认证申请" @ok="submitReject">
      <a-textarea v-model:value="rejectRemark" :rows="3" placeholder="请填写驳回原因，例如：学生证照片不清晰"></a-textarea>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { message } from 'ant-design-vue';
import { listApi, auditApi } from '/@/api/runnerVerify';
import {getFormatTime} from '/@/utils/'

const STATUS_TEXT: Record<string, string> = {'0': '待审核', '1': '已通过', '2': '已驳回'}
const statusText = (s: string) => STATUS_TEXT[s] || '未知'
const statusColor = (s: string) => s === '0' ? 'blue' : (s === '1' ? 'green' : 'red')

const columns = reactive([
  { title: '序号', dataIndex: 'index', key: 'index', width: 60 },
  { title: '申请人', dataIndex: 'username', key: 'username',
    customRender: ({ record }: any) => record.nickname ? `${record.nickname}(${record.username})` : record.username },
  { title: '真实姓名', dataIndex: 'realName', key: 'realName' },
  { title: '学号', dataIndex: 'studentNo', key: 'studentNo' },
  { title: '院系', dataIndex: 'college', key: 'college',
    customRender: ({ text }: any) => text || '--' },
  { title: '联系电话', dataIndex: 'mobile', key: 'mobile' },
  { title: '服务区域', dataIndex: 'serviceArea', key: 'serviceArea',
    customRender: ({ text }: any) => text || '--' },
  { title: '服务时段', dataIndex: 'serviceTime', key: 'serviceTime',
    customRender: ({ text }: any) => text || '--' },
  { title: '状态', dataIndex: 'status', key: 'status', width: 100 },
  { title: '审核意见', dataIndex: 'auditRemark', key: 'auditRemark' },
  { title: '申请时间', dataIndex: 'createTime', key: 'createTime',
    customRender: ({ text }: any) => getFormatTime(text, true) },
  { title: '操作', dataIndex: 'action', key: 'operation', align: 'center' as const, fixed: 'right' as const, width: 140 },
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
  listApi({ status: data.status })
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
  auditApi({ id: record.id, status: status, auditRemark: status === '1' ? '认证通过' : '' })
      .then((res: any) => {
        message.success(status === '1' ? '已通过认证' : '已驳回');
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
  auditApi({ id: currentRecord.value.id, status: '2', auditRemark: rejectRemark.value })
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