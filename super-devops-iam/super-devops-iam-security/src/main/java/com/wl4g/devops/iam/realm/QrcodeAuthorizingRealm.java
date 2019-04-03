package com.wl4g.devops.iam.realm;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import com.wl4g.devops.iam.authc.QrcodeAuthenticationToken;
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
public class QrcodeAuthorizingRealm extends AbstractIamAuthorizingRealm<QrcodeAuthenticationToken> {

	public QrcodeAuthorizingRealm(IamBasedMatcher matcher, IamContextManager manager) {
		super(matcher, manager);
	}

	/**
	 * Authenticates a user and retrieves its information.
	 * 
	 * @param token
	 *            the authentication token
	 * @throws AuthenticationException
	 *             if there is an error during authentication.
	 */
	@Override
	protected AuthenticationInfo doAuthenticationInfo(QrcodeAuthenticationToken token) throws AuthenticationException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Retrieves the AuthorizationInfo for the given principals (the CAS
	 * previously authenticated user : id + attributes).
	 * 
	 * @param principals
	 *            the primary identifying principals of the AuthorizationInfo
	 *            that should be retrieved.
	 * @return the AuthorizationInfo associated with this principals.
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		throw new UnsupportedOperationException();
	}

}
