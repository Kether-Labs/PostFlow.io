import { z } from "zod"

// ── Schéma Connexion ────────────────────────────────────
export const loginSchema = z.object({
    email: z
        .string()
        .min(1, "L'email est requis")
        .email("Format d'email invalide"),
    password: z
        .string()
        .min(1, "Le mot de passe est requis")
        .min(6, "Le mot de passe doit contenir au moins 6 caractères"),
})

export type LoginFormData = z.infer<typeof loginSchema>

// ── Schéma Inscription ──────────────────────────────────
export const registerSchema = z
    .object({
        name: z
        .string()
        .min(1, "Le nom est requis")
        .min(2, "Le nom doit contenir au moins 2 caractères")
        .max(50, "Le nom ne peut pas dépasser 50 caractères"),
        email: z
        .string()
        .min(1, "L'email est requis")
        .email("Format d'email invalide"),
        password: z
        .string()
        .min(1, "Le mot de passe est requis")
        .min(8, "Le mot de passe doit contenir au moins 8 caractères")
        .regex(
            /[A-Z]/,
            "Le mot de passe doit contenir au moins une majuscule"
        )
        .regex(
            /[0-9]/,
            "Le mot de passe doit contenir au moins un chiffre"
        ),
        confirmPassword: z
        .string()
        .min(1, "La confirmation est requise"),
    })
    .refine(
        (data) => data.password === data.confirmPassword,
        {
        message: "Les mots de passe ne correspondent pas",
        path: ["confirmPassword"],
        }
    )

export type RegisterFormData = z.infer<typeof registerSchema>