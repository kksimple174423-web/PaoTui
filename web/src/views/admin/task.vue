<template>
  <div>
    <!--页面区域-->
    <div class="page-view">
      <div class="table-operations">
        <a-space>
          <a-button type="primary" @click="handleAdd">新增任务</a-button>
          <a-button @click="handleBatchDelete">批量删除</a-button>
          <a-input-search addon-before="标题" enter-button @search="onSearch" @change="onSearchChange" />
        </a-space>
      </div>
      <a-table
          size="middle"
          rowKey="id"
          :loading="data.loading"
          :columns="columns"
          :data-source="data.dataList"
          :scroll="{ x: 'max-content' }"
          :row-selection="rowSelection"
          :pagination="{
          size: 'default',
          current: data.page,
          pageSize: data.pageSize,
          onChange: (current) => (data.page = current),
          showSizeChanger: false,
          showTotal: (total) => `共${total}条数据`,
        }"
      >
        <template #bodyCell="{ text, record, index, column }">
          <template v-if="column.key === 'operation'">
            <span>
              <a @click="handleEdit(record)">编辑</a>
              <a-divider type="vertical" />
              <a-popconfirm title="确定删除?" ok-text="是" cancel-text="否" @confirm="confirmDelete(record)">
                <a href="#">删除</a>
              </a-popconfirm>
            </span>
          </template>
          <template v-else-if="column.key === 'reward'">
            <span style="color: #ff7b31;font-weight: 600;">¥ {{ text }}</span>
          </template>
          <template v-else-if="column.key === 'status'">
            <a-tag :color="statusColor(text)">{{ statusText(text) }}</a-tag>
          </template>
        </template>
      </a-table>
    </div>

    <!--弹窗区域-->
    <div>
      <a-modal
          :visible="modal.visile"
          :forceRender="true"
          :title="modal.title"
          width="880px"
          ok-text="确认"
          cancel-text="取消"
          @cancel="handleCancel"
          @ok="handleOk"
      >
        <div>
          <a-form ref="myform" :label-col="{ style: { width: '96px' } }" :model="modal.form" :rules="modal.rules">
            <a-row :gutter="24">
              <a-col span="24">
                <a-form-item label="任务标题" name="title">
                  <a-input placeholder="例如：代取快递：中通小件一件" v-model:value="modal.form.title"></a-input>
                </a-form-item>
              </a-col>
              <a-col span="12">
                <a-form-item label="任务类型" name="classificationId">
                  <a-select placeholder="请选择"
                            allowClear
                            :options="modal.cData"
                            :field-names="{ label: 'title', value: 'id',}"
                            v-model:value="modal.form.classificationId">
                  </a-select>
                </a-form-item>
              </a-col>
              <a-col span="12">
                <a-form-item label="悬赏金额" name="reward">
                  <a-input-number placeholder="请输入" :min="0" v-model:value="modal.form.reward" style="width: 100%;"></a-input-number>
                </a-form-item>
              </a-col>
              <a-col span="12">
                <a-form-item label="取件地址" name="pickupAddress">
                  <a-input placeholder="例如：菜鸟驿站(东门店)" v-model:value="modal.form.pickupAddress"></a-input>
                </a-form-item>
              </a-col>
              <a-col span="12">
                <a-form-item label="送达地址" name="deliveryAddress">
                  <a-input placeholder="例如：3号宿舍楼" v-model:value="modal.form.deliveryAddress"></a-input>
                </a-form-item>
              </a-col>
              <a-col span="12">
                <a-form-item label="期望送达">
                  <a-input placeholder="例如：今天 18:00 前" v-model:value="modal.form.expectTime"></a-input>
                </a-form-item>
              </a-col>
              <a-col span="12">
                <a-form-item label="联系电话">
                  <a-input placeholder="请输入" v-model:value="modal.form.contactPhone"></a-input>
                </a-form-item>
              </a-col>
              <a-col span="12">
                <a-form-item label="物品规格">
                  <a-input placeholder="例如：小件 / 中号纸箱" v-model:value="modal.form.weight"></a-input>
                </a-form-item>
              </a-col>
              <a-col span="12">
                <a-form-item label="标签">
                  <a-select mode="multiple" placeholder="请选择" allowClear v-model:value="modal.form.tags">
                    <template v-for="item in modal.tagData">
                      <a-select-option :value="item.id">{{item.title}}</a-select-option>
                    </template>
                  </a-select>
                </a-form-item>
              </a-col>
              <a-col span="24">
                <a-form-item label="物品图片">
                  <a-upload-dragger
                      name="file"
                      accept="image/*"
                      :multiple="false"
                      :before-upload="beforeUpload"
                      v-model:file-list="fileList"
                  >
                    <p class="ant-upload-drag-icon">
                      <template v-if="modal.form.coverUrl">
                        <img :src="modal.form.coverUrl"  style="width: 80px;height: 60px;object-fit: cover;"/>
                      </template>
                      <template v-else>
                        <file-image-outlined />
                      </template>
                    </p>
                    <p class="ant-upload-text">请选择要上传的物品图片</p>
                  </a-upload-dragger>
                </a-form-item>
              </a-col>
              <a-col span="24">
                <a-form-item label="任务描述">
                  <a-textarea placeholder="请输入" v-model:value="modal.form.description"></a-textarea>
                </a-form-item>
              </a-col>
              <a-col span="12">
                <a-form-item label="任务状态" name="status">
                  <a-select placeholder="请选择" allowClear v-model:value="modal.form.status">
                    <a-select-option value="0">待接单</a-select-option>
                    <a-select-option value="1">已接单</a-select-option>
                    <a-select-option value="2">配送中</a-select-option>
                    <a-select-option value="3">已送达</a-select-option>
                    <a-select-option value="4">已完成</a-select-option>
                    <a-select-option value="5">已取消</a-select-option>
                    <a-select-option value="6">纠纷中</a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
              <a-col span="12">
                <a-form-item label="备注">
                  <a-input placeholder="选填" v-model:value="modal.form.remark"></a-input>
                </a-form-item>
              </a-col>
            </a-row>
          </a-form>
        </div>
      </a-modal>
    </div>
  </div>
</template>

<script setup lang="ts">
import { FormInstance, message } from 'ant-design-vue';
import { listApi, deleteApi, adminCreateApi, adminUpdateApi } from '/@/api/task';
import {listApi as listClassificationApi} from '/@/api/classification'
import {listApi as listTagApi} from '/@/api/tag'
import {BASE_URL} from "/@/store/constants";
import {getFormatTime} from '/@/utils/'
import { FileImageOutlined } from '@ant-design/icons-vue';

const STATUS_TEXT: Record<string, string> = {
  '0': '待接单', '1': '已接单', '2': '配送中', '3': '已送达', '4': '已完成', '5': '已取消', '6': '纠纷中'
}
const statusText = (s: string) => STATUS_TEXT[s] || '未知'
const statusColor = (s: string) => {
  if (s === '0') return 'blue'
  if (s === '4') return 'green'
  if (s === '5') return 'default'
  if (s === '6') return 'red'
  return 'orange'
}

const columns = reactive([

  {
    title: '序号',
    dataIndex: 'index',
    key: 'index',
    width: 60
  },
  {
    title: '任务标题',
    dataIndex: 'title',
    key: 'title'
  },
  {
    title: '任务类型',
    dataIndex: 'classificationTitle',
    key: 'classificationTitle'
  },
  {
    title: '悬赏',
    dataIndex: 'reward',
    key: 'reward',
    width: 90
  },
  {
    title: '取送路线',
    dataIndex: 'route',
    key: 'route',
    customRender: ({ record }: any) => `${record.pickupAddress || '--'} → ${record.deliveryAddress || '--'}`
  },
  {
    title: '发布者',
    dataIndex: 'publisherName',
    key: 'publisherName',
    customRender: ({ text }: any) => text || '--'
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    width: 100
  },
  {
    title: '发布时间',
    dataIndex: 'createTime',
    key: 'createTime',
    customRender: ({ text }: any) => getFormatTime(text, true)
  },
  {
    title: '操作',
    dataIndex: 'action',
    key: 'operation',
    align: 'center' as const,
    fixed: 'right' as const,
    width: 140,
  },
]);

const beforeUpload = (file: File) => {
  const fileName = new Date().getTime().toString() + '.' + file.type.substring(6);
  const copyFile = new File([file], fileName);
  modal.form.imageFile = copyFile;
  return false;
};

const fileList = ref<any[]>([]);

const data = reactive({
  dataList: [],
  loading: false,
  keyword: '',
  selectedRowKeys: [] as any[],
  pageSize: 10,
  page: 1,
});

const modal = reactive({
  visile: false,
  editFlag: false,
  title: '',
  cData: [] as any[],
  tagData: [] as any[],
  form: {
    id: undefined,
    title: undefined,
    classificationId: undefined,
    reward: undefined,
    pickupAddress: undefined,
    deliveryAddress: undefined,
    expectTime: undefined,
    contactPhone: undefined,
    weight: undefined,
    remark: undefined,
    tags: [] as any[],
    status: undefined,
    cover: undefined,
    coverUrl: undefined,
    imageFile: undefined,
    description: undefined
  },
  rules: {
    title: [{ required: true, message: '请输入任务标题', trigger: 'change' }],
    classificationId: [{ required: true, message: '请选择任务类型', trigger: 'change' }],
    reward: [{ required: true, message: '请输入悬赏金额', trigger: 'change' }],
    pickupAddress: [{ required: true, message: '请输入取件地址', trigger: 'change' }],
    deliveryAddress: [{ required: true, message: '请输入送达地址', trigger: 'change' }],
    status: [{ required: true, message: '请选择任务状态', trigger: 'change' }]
  },
});

const myform = ref<FormInstance>();

onMounted(() => {
  getDataList();
  getCDataList();
  getTagDataList();
});

const getDataList = () => {
  data.loading = true;
  listApi({
    keyword: data.keyword,
  })
      .then((res: any) => {
        data.loading = false;
        res.data.forEach((item: any, index: any) => {
          item.index = index + 1;
        });
        data.dataList = res.data;
      })
      .catch((err: any) => {
        data.loading = false;
        console.log(err);
      });
}

const getCDataList = () => {
  listClassificationApi({}).then((res: any) => {
    modal.cData = res.data
  })
}
const getTagDataList = ()=> {
  listTagApi({}).then((res: any) => {
    res.data.forEach((item: any, index: number) => {
      item.index = index + 1
    })
    modal.tagData = res.data
  })
}

const onSearchChange = (e: any) => {
  data.keyword = e?.target?.value;
};

const onSearch = () => {
  getDataList();
};

const rowSelection = ref({
  onChange: (selectedRowKeys: (string | number)[], selectedRows: any[]) => {
    data.selectedRowKeys = selectedRowKeys;
  },
});

const handleAdd = () => {
  resetModal();
  modal.visile = true;
  modal.editFlag = false;
  modal.title = '新增任务';
  for (const key in modal.form) {
    modal.form[key] = undefined;
  }
  modal.form.tags = [];
  modal.form.status = '0';
};
const handleEdit = (record: any) => {
  resetModal();
  modal.visile = true;
  modal.editFlag = true;
  modal.title = '编辑任务';
  for (const key in modal.form) {
    modal.form[key] = undefined;
  }
  modal.form.tags = [];
  for (const key in record) {
    if (record[key] !== null && record[key] !== undefined) {
      modal.form[key] = record[key];
    }
  }
  if (modal.form.cover) {
    modal.form.coverUrl = BASE_URL + '/api/staticfiles/image/' + modal.form.cover
    modal.form.cover = undefined
  }
};

const confirmDelete = (record: any) => {
  deleteApi({ ids: record.id })
      .then((res: any) => {
        message.success('删除成功');
        getDataList();
      })
      .catch((err: any) => {
        message.error(err.msg || '操作失败');
      });
};

const handleBatchDelete = () => {
  if (data.selectedRowKeys.length <= 0) {
    message.warn('请勾选删除项');
    return;
  }
  deleteApi({ ids: data.selectedRowKeys.join(',') })
      .then((res: any) => {
        message.success('删除成功');
        data.selectedRowKeys = [];
        getDataList();
      })
      .catch((err: any) => {
        message.error(err.msg || '操作失败');
      });
};

const handleOk = () => {
  myform.value
      ?.validate()
      .then(() => {
        const formData = new FormData();
        if (modal.editFlag) {
          formData.append('id', String(modal.form.id))
        }
        formData.append('title', modal.form.title)
        formData.append('classificationId', modal.form.classificationId)
        formData.append('reward', String(modal.form.reward || ''))
        formData.append('pickupAddress', modal.form.pickupAddress || '')
        formData.append('deliveryAddress', modal.form.deliveryAddress || '')
        formData.append('expectTime', modal.form.expectTime || '')
        formData.append('contactPhone', modal.form.contactPhone || '')
        formData.append('weight', modal.form.weight || '')
        formData.append('remark', modal.form.remark || '')
        formData.append('description', modal.form.description || '')
        if (modal.form.tags) {
          formData.append('tags', modal.form.tags.join(','))
        }
        if (modal.form.imageFile) {
          formData.append('imageFile', modal.form.imageFile)
        }
        if (modal.form.status) {
          formData.append('status', modal.form.status)
        }
        const api = modal.editFlag ? adminUpdateApi : adminCreateApi
        api(formData)
            .then((res: any) => {
              message.success(modal.editFlag ? '更新成功' : '创建成功');
              hideModal();
              getDataList();
            })
            .catch((err: any) => {
              message.error(err.msg || '操作失败');
            });
      })
      .catch(() => {
        console.log('表单校验未通过');
      });
};

const handleCancel = () => {
  hideModal();
};

const resetModal = () => {
  myform.value?.resetFields();
  fileList.value = []
};

const hideModal = () => {
  modal.visile = false;
};
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

.table-operations > button {
  margin-right: 8px;
}
</style>