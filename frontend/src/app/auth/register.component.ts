import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {
  registerForm: FormGroup;
  error: string = '';

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.registerForm = this.formBuilder.group({
      nom: ['', Validators.required],
      prenom: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', Validators.required],
      telephone: ['', [Validators.required, Validators.pattern('^[0-9]{8}$')]]
    }, {
      validator: this.mustMatch('password', 'confirmPassword')
    });
  }

  ngOnInit() {
    if (this.authService.isAuthenticated()) {
      this.router.navigate(['/dashboard']);
    }
  }

  mustMatch(controlName: string, matchingControlName: string) {
    return (formGroup: FormGroup) => {
      const control = formGroup.controls[controlName];
      const matchingControl = formGroup.controls[matchingControlName];

      if (matchingControl.errors && !matchingControl.errors['mustMatch']) {
        return;
      }

      if (control.value !== matchingControl.value) {
        matchingControl.setErrors({ mustMatch: true });
      } else {
        matchingControl.setErrors(null);
      }
    }
  }

  onSubmit() {
    if (this.registerForm.valid) {
      const userData = {
        nom: this.registerForm.get('nom')?.value,
        prenom: this.registerForm.get('prenom')?.value,
        email: this.registerForm.get('email')?.value,
        password: this.registerForm.get('password')?.value,
        telephone: this.registerForm.get('telephone')?.value
      };

      this.authService.register(userData).subscribe({
        next: (response) => {
          this.router.navigate(['/login'], { queryParams: { registered: true }});
        },
        error: (error) => {
          this.error = error.error.message || 'Une erreur est survenue lors de l\'inscription';
        }
      });
    }
  }

  goToLogin() {
    this.router.navigate(['/login']);
  }
}
