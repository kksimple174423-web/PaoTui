// 任务收藏
import { get, post } from '/@/utils/http/axios';

enum URL {
    userCollectList = '/api/taskCollect/getUserCollectList',
    collect = '/api/taskCollect/collect',
    unCollect = '/api/taskCollect/unCollect',
}

const collectApi = async (data: any) => post<any>({ url: URL.collect, params: {}, data: data, headers: { 'Content-Type': 'multipart/form-data;charset=utf-8' } });
const unCollectApi = async (params: any) => post<any>({ url: URL.unCollect, params: params, headers: { 'Content-Type': 'multipart/form-data;charset=utf-8' } });
const userCollectListApi = async (params: any) => get<any>({ url: URL.userCollectList, params: params });

export { collectApi, unCollectApi, userCollectListApi };