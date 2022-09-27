package twirl.helper

import actions.RequestAttrsKeys
import models.account.AccountModel
import play.api.mvc.RequestHeader

/**
 * LoggerAccount.
 *
 * @author Pierre Adam
 * @since 20.06.04
 */
object LoggedAccount {

  def get(implicit request: RequestHeader): AccountModel =
    request.attrs.apply(RequestAttrsKeys.ACCOUNT_MODEL.asScala())

  def getOption(implicit request: RequestHeader): Option[AccountModel] =
    request.attrs.get(RequestAttrsKeys.ACCOUNT_MODEL.asScala())

  def getRealAccountOption(implicit request: RequestHeader): Option[AccountModel] =
    request.attrs.get(RequestAttrsKeys.REAL_ACCOUNT.asScala())

  def getProfilePicture(implicit request: RequestHeader): String =
    request.attrs.apply(RequestAttrsKeys.PROFILE_PICTURE.asScala())
}
