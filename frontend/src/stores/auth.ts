
import { ref } from 'vue'
import { defineStore } from 'pinia'
import http from '../api/http'

interface LoginResponse {
    id: number
    name: string
    email: string
    role: string
    token: string
    tokenType: string
}

interface CurrentUser {
    email: string
}

export const useAuthStore = defineStore('auth', () => {
    const token = ref<string | null>(
        localStorage.getItem('access_token'),
    )

    const user = ref<CurrentUser | null>(null)
    const isLoggedIn = ref(false)

    async function login(email: string, password: string) {
        const response = await http.post<LoginResponse>(
            '/auth/login',
            { email, password },
        )

        token.value = response.data.token
        localStorage.setItem('access_token', response.data.token)

        await fetchCurrentUser()
    }

    async function fetchCurrentUser() {
        if (!token.value) {
            return
        }

        try {
            const response = await http.get<CurrentUser>('/auth/me')
            user.value = response.data
            isLoggedIn.value = true
        } catch (error) {
            logout()
            throw error
        }
    }

    function logout() {
        token.value = null
        user.value = null
        isLoggedIn.value = false
        localStorage.removeItem('access_token')
    }

    if (typeof window !== 'undefined') {
        window.addEventListener('auth:unauthorized', () => {
            token.value = null
            user.value = null
            isLoggedIn.value = false
        })
    }

    return {
        token,
        user,
        isLoggedIn,
        login,
        fetchCurrentUser,
        logout,
    }
})
