import { z } from 'zod';

export const registerSchema = z
  .object({
    companyCode: z
      .string()
      .trim()
      .min(1, 'errors.required_company_code')
      .regex(/^[A-Z]{3}-\d{4}$/, 'errors.invalid_company_code'),
    name: z.string().trim().min(2, 'errors.name_short'),
    email: z
      .string()
      .trim()
      .min(1, 'errors.required_email')
      .email('errors.invalid_email')
      .transform((v) => v.toLowerCase()),
    password: z
      .string()
      .min(8, 'errors.password_min_8')
      .regex(/[A-Z]/, 'errors.uppercaseRequired:_password'),
    confirmPassword: z.string(),
    acceptTerms: z.boolean(),
  })
  .refine((data) => data.password === data.confirmPassword, {
    message: 'errors.password_match',
    path: ['confirmPassword'],
  })
  .refine((data) => data.acceptTerms === true, {
    message: 'signup.mustAgreeTerms',
    path: ['acceptTerms'],
  });

export default registerSchema;
