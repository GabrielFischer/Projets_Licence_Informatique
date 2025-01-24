/* This file is part of Lwt, released under the MIT license. See LICENSE.md for
   details, or visit https://github.com/ocsigen/lwt/blob/master/LICENSE.md. */



#include "lwt_config.h"

#if !defined(LWT_ON_WINDOWS)

#include <caml/mlvalues.h>
#include <caml/unixsupport.h>
#include <unistd.h>

#include "lwt_unix.h"

struct job_isatty {
    struct lwt_unix_job job;
    int fd;
    int result;
};

static void worker_isatty(struct job_isatty *job)
{
    job->result = isatty(job->fd);
}

static value result_isatty(struct job_isatty *job)
{
    value result = Val_bool(job->result);
    lwt_unix_free_job(&job->job);
    return result;
}

CAMLprim value lwt_unix_isatty_job(value val_fd)
{
    LWT_UNIX_INIT_JOB(job, isatty, 0);
    job->fd = Int_val(val_fd);
    return lwt_unix_alloc_job(&(job->job));
}
#endif
