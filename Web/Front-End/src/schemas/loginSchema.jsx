import { z } from 'zod';

export const loginSchema = z.object({
  email: z
    .string()
    .trim()
    .min(1, 'errors.required_email')
    .email('errors.invalid_email')
    .transform((v) => v.trim().toLowerCase()),
  password: z.string().min(6, 'errors.password_min'),
  companyCode: z
    .string()
    .trim()
    .min(1, 'errors.required_company_code')
    .regex(/^[A-Z]{3}-\d{4}$/, 'errors.invalid_company_code'),
});
//String: indica que debe ser string
//trim: elimina espacios al comienzo o al final del campo
//min: valida que el estring no este vacio
//email: valida que tenga el formato email xxx@xxx.com
//transform: pasa de minuscula a mayuscula o viceversa