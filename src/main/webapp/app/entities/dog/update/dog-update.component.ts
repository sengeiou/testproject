import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IDog, Dog } from '../dog.model';
import { DogService } from '../service/dog.service';

@Component({
  selector: 'jhi-dog-update',
  templateUrl: './dog-update.component.html',
})
export class DogUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]],
    description: [],
    created: [],
  });

  constructor(protected dogService: DogService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ dog }) => {
      this.updateForm(dog);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const dog = this.createFromForm();
    if (dog.id !== undefined) {
      this.subscribeToSaveResponse(this.dogService.update(dog));
    } else {
      this.subscribeToSaveResponse(this.dogService.create(dog));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IDog>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(dog: IDog): void {
    this.editForm.patchValue({
      id: dog.id,
      name: dog.name,
      description: dog.description,
      created: dog.created,
    });
  }

  protected createFromForm(): IDog {
    return {
      ...new Dog(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      description: this.editForm.get(['description'])!.value,
      created: this.editForm.get(['created'])!.value,
    };
  }
}
