
import axios from 'axios'

const http = axios.create({
    baseURL: 'http://localhost:8080/api',
    headers: {
        'Content-Type': 'application/json',
    },
})

http.interceptors.request.use((config) => {
    const token = localStorage.getItem('access_token')

    if (token) {
        config.headers.Authorization = `Bearer ${token}`
    }

    return config
})

http.interceptors.response.use(
    (response) => response,
    (error: unknown) => {
        if (
            axios.isAxiosError(error) &&
            error.response?.status === 401
        ) {
            localStorage.removeItem('access_token')
            window.dispatchEvent(new Event('auth:unauthorized'))
        }

        return Promise.reject(error)
    },
)

export default http
