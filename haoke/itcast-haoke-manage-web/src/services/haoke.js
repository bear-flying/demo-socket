import request from '@/utils/request';//引入了request（通用的请求工具包）

export async function addHouseResource(params) {
  return request('/haoke/house/resources', {
    method: 'POST',
    body: params
  });
}

export async function updateHouseResource(params) {
  return request('/haoke/house/resources', {
    method: 'PUT',
    body: params
  });
}
