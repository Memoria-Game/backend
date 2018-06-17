import filters.CORSFilter
import javax.inject.Inject
import play.api.http.{DefaultHttpFilters, EnabledFilters}
import play.filters.gzip.GzipFilter

class Filters @Inject()(
                         defaultFilters: EnabledFilters,
                         gzip: GzipFilter,
                         log: AuthFilter,
                         cors: CORSFilter
                       ) extends DefaultHttpFilters(defaultFilters.filters :+ gzip :+ cors :+ log : _*)