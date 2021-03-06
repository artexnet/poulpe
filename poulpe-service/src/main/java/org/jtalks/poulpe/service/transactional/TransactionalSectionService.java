/**
 * Copyright (C) 2011  JTalks.org Team
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.jtalks.poulpe.service.transactional;

import java.util.List;

import org.jtalks.common.service.transactional.AbstractTransactionalEntityService;
import org.jtalks.poulpe.model.dao.SectionDao;
import org.jtalks.poulpe.model.entity.PoulpeSection;
import org.jtalks.poulpe.service.SectionService;

/**
 * Implementation of {@link SectionService}
 * 
 * @author Tatiana Birina
 */
public class TransactionalSectionService extends AbstractTransactionalEntityService<PoulpeSection, SectionDao>
        implements SectionService {

    /**
     * Create an instance of entity based service
     * 
     * @param sectionDao - data access object
     * @param validator entity validator
     */
    public TransactionalSectionService(SectionDao sectionDao) {
        this.dao = sectionDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PoulpeSection> getAll() {
        return dao.getAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveSection(PoulpeSection section) {
        dao.saveOrUpdate(section);
    }

    /** {@inheritDoc} */
    @Override
    public boolean deleteRecursively(PoulpeSection victim) {
        return dao.deleteRecursively(victim);
    }

    /** {@inheritDoc} */
    @Override
    public boolean deleteAndMoveBranchesTo(PoulpeSection victim, PoulpeSection recipient) {
        if (victim.getId() == recipient.getId()) {
            throw new IllegalArgumentException("Victim and recipient can't be the same section");
        }
        return dao.deleteAndMoveBranchesTo(victim, recipient);
    }

}
