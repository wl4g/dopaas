package com.wl4g.devops.iam.realm;

import org.apache.shiro.realm.AuthorizingRealm;

import com.wl4g.devops.iam.authc.WechatMpAuthenticationToken;
import com.wl4g.devops.iam.authc.credential.IamBasedMatcher;
import com.wl4g.devops.iam.config.BasedContextConfiguration.IamContextManager;

/**
 * This realm implementation acts as a CAS client to a CAS server for
 * authentication and basic authorization.
 * <p/>
 * This realm functions by inspecting a submitted
 * {@link org.apache.shiro.cas.CasToken CasToken} (which essentially wraps a CAS
 * service ticket) and validates it against the CAS server using a configured
 * CAS {@link org.jasig.cas.client.validation.TicketValidator TicketValidator}.
 * <p/>
 * The {@link #getValidationProtocol() validationProtocol} is {@code CAS} by
 * default, which indicates that a a
 * {@link org.jasig.cas.client.validation.Cas20ServiceTicketValidator
 * Cas20ServiceTicketValidator} will be used for ticket validation. You can
 * alternatively set or
 * {@link org.jasig.cas.client.validation.Saml11TicketValidator
 * Saml11TicketValidator} of CAS client. It is based on {@link AuthorizingRealm
 * AuthorizingRealm} for both authentication and authorization. User id and
 * attributes are retrieved from the CAS service ticket validation response
 * during authentication phase. Roles and permissions are computed during
 * authorization phase (according to the attributes previously retrieved).
 *
 * @since 1.2
 */
public class WechatMpAuthorizingRealm extends Oauth2SnsAuthorizingRealm<WechatMpAuthenticationToken> {

	public WechatMpAuthorizingRealm(IamBasedMatcher matcher, IamContextManager manager) {
		super(matcher, manager);
	}

}
