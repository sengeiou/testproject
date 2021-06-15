import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IEcho, Echo } from '../echo.model';
import { EchoService } from '../service/echo.service';

@Component({
  selector: 'jhi-echo-update',
  templateUrl: './echo-update.component.html',
})
export class EchoUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    name: [],
    description: [],
    created: [],
  });

  constructor(protected echoService: EchoService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ echo }) => {
      this.updateForm(echo);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const echo = this.createFromForm();
    if (echo.id !== undefined) {
      this.subscribeToSaveResponse(this.echoService.update(echo));
    } else {
      this.subscribeToSaveResponse(this.echoService.create(echo));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IEcho>>): void {
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

  protected updateForm(echo: IEcho): void {
    this.editForm.patchValue({
      id: echo.id,
      name: echo.name,
      description: echo.description,
      created: echo.created,
    });
  }

  protected createFromForm(): IEcho {
    return {
      ...new Echo(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      description: this.editForm.get(['description'])!.value,
      created: this.editForm.get(['created'])!.value,
    };
  }
}
