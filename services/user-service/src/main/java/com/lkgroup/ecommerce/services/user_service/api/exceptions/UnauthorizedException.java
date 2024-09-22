/*
 * AdvaHealth Solutions Pty. Ltd. ("AHS") CONFIDENTIAL
 * Copyright (c) 2022 AdvaHealth Solutions Pty. Ltd. All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains the property of AHS. The intellectual and technical concepts contained
 * herein are proprietary to AHS and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material is strictly forbidden unless prior written permission is obtained
 * from AHS.  Access to the source code contained herein is hereby forbidden to anyone except current AHS employees, managers or contractors who have executed
 * Confidentiality and Non-disclosure agreements explicitly covering such access.
 *
 * The copyright notice above does not evidence any actual or intended publication or disclosure  of  this source code, which includes
 * information that is confidential and/or proprietary, and is a trade secret, of AHS. ANY REPRODUCTION, MODIFICATION, DISTRIBUTION, PUBLIC  PERFORMANCE,
 * OR PUBLIC DISPLAY OF OR THROUGH USE  OF THIS  SOURCE CODE  WITHOUT  THE EXPRESS WRITTEN CONSENT OF COMPANY IS STRICTLY PROHIBITED, AND IN VIOLATION OF APPLICABLE
 * LAWS AND INTERNATIONAL TREATIES.  THE RECEIPT OR POSSESSION OF  THIS SOURCE CODE AND/OR RELATED INFORMATION DOES NOT CONVEY OR IMPLY ANY RIGHTS
 * TO REPRODUCE, DISCLOSE OR DISTRIBUTE ITS CONTENTS, OR TO MANUFACTURE, USE, OR SELL ANYTHING THAT IT  MAY DESCRIBE, IN WHOLE OR IN PART.
 */

package com.lkgroup.ecommerce.services.user_service.api.exceptions;


import com.lkgroup.ecommerce.common.domain.exceptions.ApplicationException;

public class UnauthorizedException extends ApplicationException {

    public static UnauthorizedException DELETION_PROTECTION_ENABLED = new UnauthorizedException("deletion_protection_enabled", "1016");
    public static UnauthorizedException OBJECT_IMMUTABLE = new UnauthorizedException("object_immutable", "1019");
    public static UnauthorizedException INVALID_CSRF_TOKEN = new UnauthorizedException("invalid_csrf_token_exception", "1002");
    public static UnauthorizedException INVALID_CORS_REQUEST = new UnauthorizedException("invalid_cors_request_exception", "TODO");
    public static UnauthorizedException OPERATION_TOKEN_INVALID = new UnauthorizedException("invalid_operation_token_exception", "1011");
    public static UnauthorizedException FORBIDDEN_IP = new UnauthorizedException("forbidden_ip_exception", "1009");
    public static UnauthorizedException ACCESS_DENIED = new UnauthorizedException("access_denied_exception", "403");
    public UnauthorizedException(String messageKey, String code) {
        super(messageKey, code, 403);
    }

}
