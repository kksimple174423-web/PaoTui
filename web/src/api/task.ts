// 校园跑腿任务系统 - 任务相关接口
import { get, post } from '/@/utils/http/axios';

enum URL {
    list = '/api/task/list',
    hall = '/api/task/hall',
    create = '/api/task/create',
    update = '/api/task/update',
    delete = '/api/task/delete',
    detail = '/api/task/detail',
    accept = '/api/task/accept',
    updateStatus = '/api/task/updateStatus',
    myPublish = '/api/task/myPublish',
    myAccept = '/api/task/myAccept',
    adminCreate = '/api/task/adminCreate',
    adminUpdate = '/api/task/adminUpdate',
}

const listApi = async (params: any) => get<any>({ url: URL.list, params: params, data: {}, headers: {} });
const hallApi = async (params: any) => get<any>({ url: URL.hall, params: params, data: {}, headers: {} });
const createApi = async (data: any) =>
    post<any>({ url: URL.create, params: {}, data: data, headers: { 'Content-Type': 'multipart/form-data;charset=utf-8' } });
const updateApi = async (data: any) =>
    post<any>({ url: URL.update, data: data, headers: { 'Content-Type': 'multipart/form-data;charset=utf-8' } });
const deleteApi = async (params: any) => post<any>({ url: URL.delete, params: params, headers: {} });
const detailApi = async (params: any) => get<any>({ url: URL.detail, params: params, headers: {} });
// 骑手接单
const acceptApi = async (params: any) => post<any>({ url: URL.accept, params: params, headers: {} });
// 更新任务状态（1已接单 2配送中 3已送达 4已完成 5已取消）
const updateStatusApi = async (params: any) => post<any>({ url: URL.updateStatus, params: params, headers: {} });
// 我发布的任务
const myPublishApi = async (params: any) => get<any>({ url: URL.myPublish, params: params, headers: {} });
// 我接的任务
const myAcceptApi = async (params: any) => get<any>({ url: URL.myAccept, params: params, headers: {} });

// 后台管理端新增/编辑（使用管理员令牌）
const adminCreateApi = async (data: any) =>
    post<any>({ url: URL.adminCreate, params: {}, data: data, headers: { 'Content-Type': 'multipart/form-data;charset=utf-8' } });
const adminUpdateApi = async (data: any) =>
    post<any>({ url: URL.adminUpdate, params: {}, data: data, headers: { 'Content-Type': 'multipart/form-data;charset=utf-8' } });

export { listApi, hallApi, createApi, updateApi, deleteApi, detailApi, acceptApi, updateStatusApi, myPublishApi, myAcceptApi, adminCreateApi, adminUpdateApi };