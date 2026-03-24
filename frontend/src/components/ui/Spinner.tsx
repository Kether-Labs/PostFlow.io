import { Loader2 } from "lucide-react";
import { cn } from "@/lib/utils";

interface SpinnerProps {
  size?: 'sm' | 'md' | 'lg'  // défaut: 'md'
    className?: string
}

const sizeClasses = {
    sm: 'w-4 h-4',
    md: 'w-6 h-6',
    lg: 'w-8 h-8'
}
const Spinner = ({ size = 'md', className }: SpinnerProps) => {
    return (
        <Loader2 className={cn(sizeClasses[size], className)} />
    );
}

export default Spinner;