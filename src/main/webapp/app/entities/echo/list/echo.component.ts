import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IEcho } from '../echo.model';
import { EchoService } from '../service/echo.service';
import { EchoDeleteDialogComponent } from '../delete/echo-delete-dialog.component';

@Component({
  selector: 'jhi-echo',
  templateUrl: './echo.component.html',
})
export class EchoComponent implements OnInit {
  echoes?: IEcho[];
  isLoading = false;
  currentSearch: string;

  constructor(protected echoService: EchoService, protected modalService: NgbModal, protected activatedRoute: ActivatedRoute) {
    this.currentSearch = this.activatedRoute.snapshot.queryParams['search'] ?? '';
  }

  loadAll(): void {
    this.isLoading = true;
    if (this.currentSearch) {
      this.echoService
        .search({
          query: this.currentSearch,
        })
        .subscribe(
          (res: HttpResponse<IEcho[]>) => {
            this.isLoading = false;
            this.echoes = res.body ?? [];
          },
          () => {
            this.isLoading = false;
          }
        );
      return;
    }

    this.echoService.query().subscribe(
      (res: HttpResponse<IEcho[]>) => {
        this.isLoading = false;
        this.echoes = res.body ?? [];
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

  trackId(index: number, item: IEcho): number {
    return item.id!;
  }

  delete(echo: IEcho): void {
    const modalRef = this.modalService.open(EchoDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.echo = echo;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
