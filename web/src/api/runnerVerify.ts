// 骑手认证
import {get, post} from '/@/utils/http/axios';

enum URL {
    apply = '/api/runnerVerify/apply',
    myApply = '/api/runnerVerify/myApply',
    list = '/api/runnerVerify/list',
    audit = '/api/runnerVerify/audit',
}

const applyApi = async (data: any) =>
    post<any>({url: URL.apply, params: {}, data: data, headers: {'Content-Type': 'multipart/form-data;charset=utf-8'}});
const myApplyApi = async (params: any) =>
    get<any>({url: URL.myApply, params: params, data: {}, headers: {}});
const listApi = async (params: any) =>
    get<any>({url: URL.list, params: params, data: {}, headers: {}});
const auditApi = async (params: any) =>
    post<any>({url: URL.audit, params: params, headers: {}});

export {applyApi, myApplyApi, listApi, auditApi};