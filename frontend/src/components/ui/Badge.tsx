
import { Badge as ShadcnBadge } from "@/components/ui/badge"
import { cn } from "@/lib/utils"
import { cva, VariantProps } from "class-variance-authority"


interface BadgeProps {
    variant: 'draft' | 'scheduled' | 'published' | 'failed'
    children?: React.ReactNode
    className?: string
}

// type BadgeProps = React.ComponentProps<typeof ShadcnBadge> &
//     VariantProps<typeof badgeVariants> & {
//         variant?: 'draft' | 'scheduled' | 'published' | 'failed'
//     }

const badgeVariants = cva("", {
    variants: {
        status: {
            draft: "bg-gray-100 text-gray-700 hover:bg-gray-100",
            scheduled: "bg-blue-100 text-blue-700 hover:bg-blue-100",
            published: "bg-green-100 text-green-700 hover:bg-green-100",
            failed: "bg-red-100 text-red-700 hover:bg-red-100",
        },
    },
})

const Badge = ({variant="draft",children,className,...props}:BadgeProps) => {
    return (
        <ShadcnBadge
            variant="outline"
                className={cn(badgeVariants({ status: variant }), className)}
                {...props}
            >
                {children}
        </ShadcnBadge>
    );
}

export default Badge;