
import http from './http'

export interface RegisterRequest {
    name: string
    email: string
    password: string
}

export interface RegisterResponse {
    id: number
    name: string
    email: string
    role: string
}

export async function registerUser(
    request: RegisterRequest,
): Promise<RegisterResponse> {
    const response = await http.post<RegisterResponse>(
        '/users',
        request,
    )

    return response.data
}
