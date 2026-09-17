import { z } from 'zod';

export const RegisterSchema = z.object({
  name: z.string().min(2, 'errors.name_short'),
  email: z.string().min(1, 'errors.required:_email').email('errors.invalid_email'),
  password: z
    .string()
    .min(8, 'errors.minLength:_password')
    .regex(/[A-Z]/, 'errors.uppercaseRequired:_password'),

  confirmPassword: z.string(),
}).refine((data) => data.password === data.confirmPassword, {
  message: 'errors.passwords_dont_match',
  path: ['confirmPassword'],
});
