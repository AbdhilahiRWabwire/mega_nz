package mega.privacy.android.app.domain.usecase.search

import mega.privacy.android.data.repository.MegaNodeRepository
import nz.mega.sdk.MegaNode
import javax.inject.Inject

/**
 * Use Case which search Nodes in CloudExplorer
 *
 * @property megaNodeRepository [MegaNodeRepository]
 * @property getSearchFromMegaNodeParentUseCase [GetSearchFromMegaNodeParentUseCase]
 */
class CloudExplorerSearchNodeUseCase @Inject constructor(
    private val megaNodeRepository: MegaNodeRepository,
    private val getSearchFromMegaNodeParentUseCase: GetSearchFromMegaNodeParentUseCase,
) {
    /**
     * Use Case which search Nodes in CloudExplorer
     * @param query Query to be searched
     * @param parentHandle
     * @param parentHandleSearch
     * @param searchType
     */
    suspend operator fun invoke(
        query: String?,
        parentHandle: Long,
        parentHandleSearch: Long,
        searchType: Int = -1
    ): List<MegaNode> {
        return query?.let {
            val parentNode = megaNodeRepository.getNodeByHandle(parentHandle)
            getSearchFromMegaNodeParentUseCase(
                query = query,
                parentHandleSearch = parentHandleSearch,
                parent = parentNode,
                searchType = searchType
            )
        } ?: run {
            emptyList()
        }
    }
}