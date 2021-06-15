import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IDog } from '../dog.model';
import { DogService } from '../service/dog.service';
import { DogDeleteDialogComponent } from '../delete/dog-delete-dialog.component';

@Component({
  selector: 'jhi-dog',
  templateUrl: './dog.component.html',
})
export class DogComponent implements OnInit {
  dogs?: IDog[];
  isLoading = false;
  currentSearch: string;

  constructor(protected dogService: DogService, protected modalService: NgbModal, protected activatedRoute: ActivatedRoute) {
    this.currentSearch = this.activatedRoute.snapshot.queryParams['search'] ?? '';
  }

  loadAll(): void {
    this.isLoading = true;
    if (this.currentSearch) {
      this.dogService
        .search({
          query: this.currentSearch,
        })
        .subscribe(
          (res: HttpResponse<IDog[]>) => {
            this.isLoading = false;
            this.dogs = res.body ?? [];
          },
          () => {
            this.isLoading = false;
          }
        );
      return;
    }

    this.dogService.query().subscribe(
      (res: HttpResponse<IDog[]>) => {
        this.isLoading = false;
        this.dogs = res.body ?? [];
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  search(query: string): void {
    this.currentSearch = query;
    this.loadAll();
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IDog): number {
    return item.id!;
  }

  delete(dog: IDog): void {
    const modalRef = this.modalService.open(DogDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.dog = dog;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
