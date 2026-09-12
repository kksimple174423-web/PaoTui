// 钱包与赏金托管
import {get, post} from '/@/utils/http/axios';

enum URL {
    my = '/api/wallet/my',
    records = '/api/wallet/records',
    recharge = '/api/wallet/recharge',
    withdraw = '/api/wallet/withdraw',
    withdrawList = '/api/wallet/withdrawList',
    allWithdraw = '/api/wallet/allWithdraw',
    auditWithdraw = '/api/wallet/auditWithdraw',
}

const myApi = async (params: any) => get<any>({url: URL.my, params: params, data: {}, headers: {}});
const recordsApi = async (params: any) => get<any>({url: URL.records, params: params, data: {}, headers: {}});
const rechargeApi = async (params: any) => post<any>({url: URL.recharge, params: params, headers: {}});
const withdrawApi = async (params: any) => post<any>({url: URL.withdraw, params: params, headers: {}});
const withdrawListApi = async (params: any) => get<any>({url: URL.withdrawList, params: params, data: {}, headers: {}});
const allWithdrawApi = async (params: any) => get<any>({url: URL.allWithdraw, params: params, data: {}, headers: {}});
const auditWithdrawApi = async (params: any) => post<any>({url: URL.auditWithdraw, params: params, headers: {}});

export {myApi, recordsApi, rechargeApi, withdrawApi, withdrawListApi, allWithdrawApi, auditWithdrawApi};