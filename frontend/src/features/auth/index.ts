// Types API (existants depuis issue #19)
// export type {
//     User,
//     LoginRequest,
//     LoginResponse,
//     RegisterRequest,
//     RegisterResponse,
//     } from "./types/auth.types"

// Schémas et types de formulaires (nouveaux)
export {
    loginSchema,
    registerSchema,
} from "./types/auth.schemas"

export type {
    LoginFormData,
    RegisterFormData,
} from "./types/auth.schemas"