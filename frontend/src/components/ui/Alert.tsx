"use client"

import { Alert as ShadcnAlert, AlertDescription, AlertTitle } from "@/components/ui/alert"
import { cn } from "@/lib/utils"
import { X, Info, CheckCircle, AlertTriangle, AlertCircle } from "lucide-react"
import { useState } from "react"

const variantConfig = {
    info: {
        icon: Info,
        className: "border-blue-500/50 text-blue-700 bg-blue-50",
    },
    success: {
        icon: CheckCircle,
        className: "border-green-500/50 text-green-700 bg-green-50",
    },
    warning: {
        icon: AlertTriangle,
        className: "border-yellow-500/50 text-yellow-700 bg-yellow-50",
    },
    error: {
        icon: AlertCircle,
        className: "border-red-500/50 text-red-700 bg-red-50",
    },
    destructive: {
        icon: AlertCircle,
        className: "border-destructive/50 text-destructive bg-destructive/10",
    },
}

interface AlertProps {
    variant?: 'info' | 'success' | 'warning' | 'error' | 'destructive'
    title?: string
    children: React.ReactNode
    dismissible?: boolean
    onDismiss?: () => void
    className?: string
}

export function Alert({
    variant = 'info',
    title,
    children,
    dismissible = false,
    onDismiss,
    className
}: AlertProps) {
    const [isVisible, setIsVisible] = useState(true)
    const config = variantConfig[variant]
    const Icon = config.icon

    if (!isVisible) return null

    const handleDismiss = () => {
        setIsVisible(false)
        onDismiss?.()
    }

    return (
        <ShadcnAlert
            className={cn(config.className, "relative", className)}
        >
        <Icon className="h-4 w-4" />
        {title && <AlertTitle>{title}</AlertTitle>}
            <AlertDescription>{children}</AlertDescription>
            {dismissible && (
                <button
                    onClick={handleDismiss}
                    className="absolute right-2 top-2 p-1 rounded-md hover:bg-black/10 transition-colors"
                >
                <X className="h-4 w-4" />
                </button>
            )}
        </ShadcnAlert>
    )
}