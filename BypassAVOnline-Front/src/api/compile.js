import request from '../utils/request.js'

export function fetchCompile(endpoint, data) {
  return request({
    url: `/v1/${endpoint}`,
    method: 'post',
    data
  })
}

export function fetchDownloadLink(endpoint) {
  return request({
  url: `/v1/${endpoint}`,
  method: 'get',
  responseType: 'blob'
})
}

