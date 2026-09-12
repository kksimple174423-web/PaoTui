// 订单双向评价
import {get, post} from '/@/utils/http/axios';

enum URL {
    list = '/api/comment/list',
    create = '/api/comment/create',
    delete = '/api/comment/delete',
    update = '/api/comment/update',
    listTaskComments = '/api/comment/listTaskComments',
    listUserComments = '/api/comment/listUserComments',
    listReceivedComments = '/api/comment/listReceivedComments',
    reply = '/api/comment/reply',
    like = '/api/comment/like'
}

const listApi = async (params: any) => get<any>({url: URL.list, params: params, data: {}, headers: {}});
const createApi = async (data: any) => post<any>({
    url: URL.create,
    params: {},
    data: data,
    headers: {'Content-Type': 'multipart/form-data;charset=utf-8'}
});
const deleteApi = async (params: any) => post<any>({url: URL.delete, params: params, headers: {}});
const updateApi = async (data: any) => post<any>({url: URL.update, data: data, headers: {'Content-Type': 'multipart/form-data;charset=utf-8'}});
const listTaskCommentsApi = async (params: any) => get<any>({url: URL.listTaskComments, params: params, data: {}, headers: {}});
const listUserCommentsApi = async (params: any) => get<any>({url: URL.listUserComments, params: params, data: {}, headers: {}});
const listReceivedCommentsApi = async (params: any) => get<any>({url: URL.listReceivedComments, params: params, data: {}, headers: {}});
const replyApi = async (params: any) => post<any>({url: URL.reply, params: params, headers: {}});
const likeApi = async (params: any) => post<any>({url: URL.like, params: params, headers: {}});

export {listApi, createApi, deleteApi, updateApi, listTaskCommentsApi, listUserCommentsApi, listReceivedCommentsApi, replyApi, likeApi};