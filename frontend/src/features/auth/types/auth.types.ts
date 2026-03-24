export interface User {
    id: string
    name: string
    email: string
}

export interface LoginRequest {
    email: string
    password: string
}

export interface LoginResponse {
    success: boolean
    user: User
    token: string
}

export interface RegisterRequest {
    name: string
    email: string
    password: string
}

export interface RegisterResponse {
    success: boolean
    message: string
    user: User
    token: string
}
