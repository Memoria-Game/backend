import javax.inject.Inject
import play.api.http.DefaultHttpFilters
import play.api.http.EnabledFilters
import play.filters.gzip.GzipFilter

class Filters @Inject()(
                         defaultFilters: EnabledFilters,
                         gzip: GzipFilter,
                         log: AuthFilter
                       ) extends DefaultHttpFilters(defaultFilters.filters :+ gzip :+ log: _*)